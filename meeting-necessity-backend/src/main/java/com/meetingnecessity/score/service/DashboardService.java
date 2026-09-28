package com.meetingnecessity.score.service;

import com.meetingnecessity.score.dto.*;
import com.meetingnecessity.score.model.Meeting;
import com.meetingnecessity.score.model.Score;
import com.meetingnecessity.score.model.Settings;
import com.meetingnecessity.score.model.Suggestion;
import com.meetingnecessity.score.model.User;
import com.meetingnecessity.score.model.enums.MeetingStatus;
import com.meetingnecessity.score.repository.MeetingRepository;
import com.meetingnecessity.score.repository.SettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    private final MeetingRepository meetingRepository;
    private final SettingsRepository settingsRepository;
    private final ScoringService scoringService;
    private final GoogleCalendarService googleCalendarService;

    public DashboardService(MeetingRepository meetingRepository, 
                            SettingsRepository settingsRepository, 
                            ScoringService scoringService,
                            GoogleCalendarService googleCalendarService) {
        this.meetingRepository = meetingRepository;
        this.settingsRepository = settingsRepository;
        this.scoringService = scoringService;
        this.googleCalendarService = googleCalendarService;
    }

    @Transactional
    public DashboardResponseDTO getDashboardData(User user) {
        // Enforce user ownership: sync and load meetings exclusively for this user
        List<Meeting> meetings = googleCalendarService.syncThisWeeksMeetings(user);

        Settings settings = settingsRepository.findByUser(user)
                .orElseGet(() -> {
                    Settings s = new Settings(user);
                    return settingsRepository.save(s);
                });

        double totalCost = 0.0;
        double flaggedCost = 0.0;
        double hoursCouldBeSaved = 0.0;
        int couldBeEmailCount = 0;

        List<MeetingDTO> meetingDTOs = new ArrayList<>();

        for (Meeting meeting : meetings) {
            // Ensure every meeting has a calculated score
            Score score = meeting.getScore();
            if (score == null) {
                score = scoringService.calculateAndSaveScore(meeting);
            }

            long durationMins = Duration.between(meeting.getStartTime(), meeting.getEndTime()).toMinutes();
            if (durationMins <= 0) durationMins = 30;

            totalCost += score.getEstimatedCost();

            if (score.getStatus() == MeetingStatus.UNNECESSARY) {
                flaggedCost += score.getEstimatedCost();
                couldBeEmailCount++;
                hoursCouldBeSaved += (durationMins / 60.0);
            } else if (score.getStatus() == MeetingStatus.BORDERLINE) {
                // Partial time savings from trimming or shortening
                hoursCouldBeSaved += (durationMins / 60.0) * 0.4;
            }

            SuggestionDTO suggestionDTO = null;
            Suggestion suggestion = score.getSuggestion();
            if (suggestion != null) {
                suggestionDTO = new SuggestionDTO(suggestion.getId(), suggestion.getSuggestionText(), suggestion.getSource());
            }

            ScoreDTO scoreDTO = new ScoreDTO(
                    score.getId(),
                    score.getNecessityScore(),
                    score.getEstimatedCost(),
                    score.getStatus(),
                    score.getCalculatedAt(),
                    suggestionDTO
            );

            MeetingDTO meetingDTO = new MeetingDTO(
                    meeting.getId(),
                    meeting.getGoogleEventId(),
                    meeting.getTitle(),
                    meeting.getStartTime(),
                    meeting.getEndTime(),
                    meeting.getAttendeesCount(),
                    meeting.getAgendaText(),
                    meeting.isRecurring(),
                    durationMins,
                    scoreDTO
            );

            meetingDTOs.add(meetingDTO);
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.with(java.time.DayOfWeek.MONDAY);
        LocalDateTime endOfWeek = startOfWeek.plusDays(6);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        String dateRange = startOfWeek.format(dtf) + " - " + endOfWeek.format(dtf);

        SettingsDTO settingsDTO = new SettingsDTO(
                settings.getCostPerPersonPerHour(),
                settings.getLowScoreThreshold(),
                settings.getHighScoreThreshold()
        );

        return new DashboardResponseDTO(
                Math.round(totalCost * 100.0) / 100.0,
                Math.round(flaggedCost * 100.0) / 100.0,
                Math.round(hoursCouldBeSaved * 10.0) / 10.0,
                meetingDTOs.size(),
                couldBeEmailCount,
                dateRange,
                settingsDTO,
                meetingDTOs
        );
    }
}
