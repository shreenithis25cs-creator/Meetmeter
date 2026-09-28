package com.meetingnecessity.score.service;

import com.meetingnecessity.score.model.Meeting;
import com.meetingnecessity.score.model.Score;
import com.meetingnecessity.score.model.Settings;
import com.meetingnecessity.score.model.Suggestion;
import com.meetingnecessity.score.model.enums.MeetingStatus;
import com.meetingnecessity.score.model.enums.SuggestionSource;
import com.meetingnecessity.score.repository.ScoreRepository;
import com.meetingnecessity.score.repository.SettingsRepository;
import com.meetingnecessity.score.repository.SuggestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class ScoringService {

    private final ScoreRepository scoreRepository;
    private final SuggestionRepository suggestionRepository;
    private final SettingsRepository settingsRepository;
    private final ClaudeApiService claudeApiService;

    public ScoringService(ScoreRepository scoreRepository, 
                          SuggestionRepository suggestionRepository, 
                          SettingsRepository settingsRepository, 
                          ClaudeApiService claudeApiService) {
        this.scoreRepository = scoreRepository;
        this.suggestionRepository = suggestionRepository;
        this.settingsRepository = settingsRepository;
        this.claudeApiService = claudeApiService;
    }

    /**
     * Calculates the necessity score and cost for a meeting, calls Claude AI for agenda clarity and suggestion,
     * and saves both Score and Suggestion entities.
     */
    @Transactional
    public Score calculateAndSaveScore(Meeting meeting) {
        Settings settings = settingsRepository.findByUser(meeting.getUser())
                .orElseGet(() -> new Settings(meeting.getUser()));

        long durationMinutes = Duration.between(meeting.getStartTime(), meeting.getEndTime()).toMinutes();
        if (durationMinutes <= 0) {
            durationMinutes = 30; // fallback default
        }

        // 1. RULE-BASED SCORING ALGORITHM
        int scoreValue = 50; // Start at 50 base points

        // Agenda text words calculation
        String agenda = meeting.getAgendaText();
        int wordCount = 0;
        if (agenda != null && !agenda.trim().isEmpty()) {
            wordCount = agenda.trim().split("\\s+").length;
        }

        // +30 if agenda text has 15+ words, -20 if agenda is empty or under 5 words
        if (wordCount >= 15) {
            scoreValue += 30;
        } else if (wordCount < 5) {
            scoreValue -= 20;
        }

        // -25 if attendeesCount >= 8, +20 if attendeesCount <= 4
        if (meeting.getAttendeesCount() >= 8) {
            scoreValue -= 25;
        } else if (meeting.getAttendeesCount() <= 4) {
            scoreValue += 20;
        }

        // -15 if duration >= 1 hour AND agenda is empty
        if (durationMinutes >= 60 && wordCount == 0) {
            scoreValue -= 15;
        }

        // +25 if isRecurring AND duration <= 20 minutes (likely a standup)
        if (meeting.isRecurring() && durationMinutes <= 20) {
            scoreValue += 25;
        }

        // Clamp final score between 0-100
        int finalScore = Math.max(0, Math.min(100, scoreValue));

        // 2. STATUS CLASSIFICATION (using user settings thresholds)
        MeetingStatus status;
        if (finalScore < settings.getLowScoreThreshold()) {
            status = MeetingStatus.UNNECESSARY;
        } else if (finalScore <= settings.getHighScoreThreshold() - 1) {
            status = MeetingStatus.BORDERLINE;
        } else {
            status = MeetingStatus.NECESSARY;
        }

        // 3. COST CALCULATION
        // cost = attendeesCount * (durationMinutes / 60.0) * user.settings.costPerPersonPerHour
        double estimatedCost = meeting.getAttendeesCount() * (durationMinutes / 60.0) * settings.getCostPerPersonPerHour();

        // 4. AI INTEGRATION & SUGGESTION
        ClaudeApiService.ClaudeAnalysisResult aiResult = claudeApiService.analyzeAgenda(
                meeting.getTitle(),
                meeting.getAgendaText(),
                meeting.getAttendeesCount(),
                durationMinutes
        );

        // Find or create Score record
        Score score = scoreRepository.findByMeeting(meeting).orElseGet(Score::new);
        score.setMeeting(meeting);
        score.setNecessityScore(finalScore);
        score.setEstimatedCost(estimatedCost);
        score.setStatus(status);
        score.setCalculatedAt(LocalDateTime.now());
        Score savedScore = scoreRepository.save(score);

        // Find or create Suggestion record
        Suggestion suggestion = suggestionRepository.findByScore(savedScore).orElseGet(Suggestion::new);
        suggestion.setScore(savedScore);
        suggestion.setSuggestionText(aiResult.getSuggestion());
        suggestion.setSource(aiResult.isSuccess() ? SuggestionSource.AI : SuggestionSource.RULE_BASED);
        suggestionRepository.save(suggestion);

        savedScore.setSuggestion(suggestion);
        meeting.setScore(savedScore);

        return savedScore;
    }
}
