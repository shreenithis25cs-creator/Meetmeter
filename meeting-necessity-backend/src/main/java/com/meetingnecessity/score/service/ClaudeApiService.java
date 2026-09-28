package com.meetingnecessity.score.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ClaudeApiService {

    private static final Logger log = LoggerFactory.getLogger(ClaudeApiService.class);

    public static class ClaudeAnalysisResult {
        private final int clarityScore;
        private final String suggestion;
        private final boolean success;

        public ClaudeAnalysisResult(int clarityScore, String suggestion, boolean success) {
            this.clarityScore = clarityScore;
            this.suggestion = suggestion;
            this.success = success;
        }

        public int getClarityScore() {
            return clarityScore;
        }

        public String getSuggestion() {
            return suggestion;
        }

        public boolean isSuccess() {
            return success;
        }
    }

    /**
     * Analyzes meeting agenda and returns a clarity score (0-100) with a suggestion
     * using rule-based if-else logic based on agenda text, attendees, and duration.
     */
    public ClaudeAnalysisResult analyzeAgenda(String meetingTitle, String agendaText, int attendeesCount, long durationMinutes) {
        log.info("Analyzing meeting '{}' with rule-based logic.", meetingTitle);

        int clarityScore;
        String suggestion;

        int wordCount = (agendaText == null) ? 0 : agendaText.trim().split("\\s+").length;

        if (wordCount < 5) {
            // Very short or no agenda
            clarityScore = 20;
            if (attendeesCount >= 8) {
                suggestion = "Convert to an asynchronous Slack memo or Loom recording to save attendee hours.";
            } else if (durationMinutes > 45) {
                suggestion = "Add a clear agenda and reduce meeting duration to under 30 minutes.";
            } else {
                suggestion = "Circulate a clear 3-bullet agenda at least 24 hours prior to meeting.";
            }
        } else if (wordCount < 15) {
            // Brief agenda
            clarityScore = 45;
            if (attendeesCount >= 10) {
                suggestion = "Trim non-essential invitees and expand agenda with clear action items.";
            } else if (durationMinutes > 60) {
                suggestion = "Break this into two shorter focused sessions with a clearer agenda.";
            } else {
                suggestion = "Expand agenda with specific outcomes and assigned owners for each topic.";
            }
        } else {
            // Detailed agenda - check other factors
            if (attendeesCount >= 8 && durationMinutes > 45) {
                clarityScore = 50;
                suggestion = "Trim non-essential invitees to 4 core decision makers and tighten to 30 minutes.";
            } else if (attendeesCount >= 8) {
                clarityScore = 65;
                suggestion = "Trim non-essential invitees to 4 core decision makers and share notes with the rest.";
            } else if (durationMinutes > 60) {
                clarityScore = 60;
                suggestion = "Split into two focused 30-minute sessions to maintain team engagement.";
            } else if (durationMinutes > 45) {
                clarityScore = 75;
                suggestion = "Tighten meeting agenda to 25 minutes to preserve team deep work focus.";
            } else if (attendeesCount <= 3) {
                clarityScore = 95;
                suggestion = "Excellent focused meeting - consider sharing a written summary afterward.";
            } else {
                clarityScore = 90;
                suggestion = "High collaborative value with clear agenda and focused attendee list.";
            }
        }

        return new ClaudeAnalysisResult(clarityScore, suggestion, true);
    }
}
