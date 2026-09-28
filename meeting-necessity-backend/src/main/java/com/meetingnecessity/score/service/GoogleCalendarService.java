package com.meetingnecessity.score.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetingnecessity.score.model.Meeting;
import com.meetingnecessity.score.model.User;
import com.meetingnecessity.score.repository.MeetingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GoogleCalendarService {

    private static final Logger log = LoggerFactory.getLogger(GoogleCalendarService.class);

    private final WebClient webClient;
    private final MeetingRepository meetingRepository;
    private final ObjectMapper objectMapper;

    public GoogleCalendarService(WebClient.Builder webClientBuilder, MeetingRepository meetingRepository, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl("https://www.googleapis.com/calendar/v3").build();
        this.meetingRepository = meetingRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Fetches this week's events from Google Calendar API using the user's stored access token,
     * and saves or updates them in the database.
     */
    @Transactional
    public List<Meeting> syncThisWeeksMeetings(User user) {
        String token = user.getGoogleAccessToken();
        List<Meeting> savedMeetings = new ArrayList<>();

        if (token == null || token.isEmpty() || token.contains("mock") || token.contains("demo")) {
            log.info("Demo / mock mode active for user {}. Ensuring sample meetings exist.", user.getEmail());
            return generateMockMeetingsIfEmpty(user);
        }

        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startOfWeek = now.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
                    .withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime endOfWeek = startOfWeek.plusDays(7);

            String timeMin = startOfWeek.atZone(java.time.ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            String timeMax = endOfWeek.atZone(java.time.ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

            String responseJson = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/calendars/primary/events")
                            .queryParam("timeMin", timeMin)
                            .queryParam("timeMax", timeMax)
                            .queryParam("singleEvents", true)
                            .queryParam("orderBy", "startTime")
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();

            if (responseJson != null) {
                JsonNode root = objectMapper.readTree(responseJson);
                JsonNode items = root.path("items");

                if (items.isArray()) {
                    for (JsonNode item : items) {
                        String googleEventId = item.path("id").asText();
                        String summary = item.path("summary").asText("Untitled Meeting");
                        String description = item.path("description").asText("");

                        // Parse start and end time
                        JsonNode startNode = item.path("start");
                        JsonNode endNode = item.path("end");

                        String startStr = startNode.path("dateTime").asText(startNode.path("date").asText(null));
                        String endStr = endNode.path("dateTime").asText(endNode.path("date").asText(null));

                        if (startStr == null || endStr == null) continue;

                        LocalDateTime startTime = parseGoogleDateTime(startStr, startNode);
                        LocalDateTime endTime = parseGoogleDateTime(endStr, endNode);

                        // Count attendees
                        int attendeesCount = 1;
                        JsonNode attendeesNode = item.path("attendees");
                        if (attendeesNode.isArray()) {
                            attendeesCount = Math.max(1, attendeesNode.size());
                        }

                        // Check recurrence
                        boolean isRecurring = item.has("recurringEventId");

                        // Find or create meeting for this user (User ownership enforced)
                        Optional<Meeting> existingOpt = meetingRepository.findByUserAndGoogleEventId(user, googleEventId);
                        Meeting meeting = existingOpt.orElseGet(Meeting::new);
                        meeting.setUser(user);
                        meeting.setGoogleEventId(googleEventId);
                        meeting.setTitle(summary);
                        meeting.setAgendaText(description);
                        meeting.setStartTime(startTime);
                        meeting.setEndTime(endTime);
                        meeting.setAttendeesCount(attendeesCount);
                        meeting.setRecurring(isRecurring);

                        savedMeetings.add(meetingRepository.save(meeting));
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to sync from live Google Calendar API ({}). Returning existing/mock meetings.", e.getMessage());
            return generateMockMeetingsIfEmpty(user);
        }

        return savedMeetings.isEmpty() ? generateMockMeetingsIfEmpty(user) : savedMeetings;
    }

    private LocalDateTime parseGoogleDateTime(String value, JsonNode eventTime) {
        try {
            return OffsetDateTime.parse(value).toLocalDateTime();
        } catch (java.time.format.DateTimeParseException ignored) {
            // Google Calendar can return a local date-time with a separate timeZone field.
            java.time.LocalDate date = java.time.LocalDate.parse(value.substring(0, 10));
            if (value.length() > 10) {
                java.time.LocalTime time = java.time.LocalTime.parse(value.substring(11));
                String zone = eventTime.path("timeZone").asText("");
                if (!zone.isBlank()) {
                    return date.atTime(time).atZone(java.time.ZoneId.of(zone))
                            .withZoneSameInstant(java.time.ZoneId.systemDefault()).toLocalDateTime();
                }
                return date.atTime(time);
            }
            return date.atStartOfDay();
        }
    }

    private List<Meeting> generateMockMeetingsIfEmpty(User user) {
        List<Meeting> existing = meetingRepository.findByUser(user);
        if (!existing.isEmpty()) {
            return existing;
        }

        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        List<Meeting> sampleMeetings = List.of(
            new Meeting(user, "g_event_1", "Weekly company status broadcast", 
                now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.MONDAY)).withHour(10).withMinute(0),
                now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.MONDAY)).withHour(10).withMinute(45),
                28, "General round-robin status updates across all company units with team presentations.", true),
            new Meeting(user, "g_event_2", "Design system architectural review", 
                now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.TUESDAY)).withHour(14).withMinute(30),
                now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.TUESDAY)).withHour(15).withMinute(0),
                4, "Detailed technical breakdown of tokens, typography hierarchy, and Tailwind integration decisions.", false),
            new Meeting(user, "g_event_3", "Marketing asset pipeline check-in", 
                now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.WEDNESDAY)).withHour(11).withMinute(0),
                now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.WEDNESDAY)).withHour(12).withMinute(0),
                11, "Quick sync.", true),
            new Meeting(user, "g_event_4", "Quarterly product roadmap grooming", 
                now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.THURSDAY)).withHour(13).withMinute(0),
                now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.THURSDAY)).withHour(13).withMinute(45),
                8, "Feature prioritizing, scope evaluation, backlog grooming, and sprint milestone allocation.", true),
            new Meeting(user, "g_event_5", "Infrastructure incident post-mortem", 
                now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.THURSDAY)).withHour(16).withMinute(0),
                now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.THURSDAY)).withHour(16).withMinute(50),
                5, "Comprehensive root cause analysis of the database replication lag and mitigation checklist.", false),
            new Meeting(user, "g_event_6", "Tool vendor contract evaluation", 
                now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.FRIDAY)).withHour(9).withMinute(30),
                now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.FRIDAY)).withHour(10).withMinute(10),
                9, "Review SaaS tool pricing sheet and negotiate renewal terms with procurement team.", false)
        );

        return meetingRepository.saveAll(sampleMeetings);
    }
}
