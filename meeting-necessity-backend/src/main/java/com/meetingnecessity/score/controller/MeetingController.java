package com.meetingnecessity.score.controller;

import com.meetingnecessity.score.dto.MeetingDTO;
import com.meetingnecessity.score.dto.ScoreDTO;
import com.meetingnecessity.score.dto.SuggestionDTO;
import com.meetingnecessity.score.model.Meeting;
import com.meetingnecessity.score.model.Score;
import com.meetingnecessity.score.model.Suggestion;
import com.meetingnecessity.score.model.User;
import com.meetingnecessity.score.repository.MeetingRepository;
import com.meetingnecessity.score.service.GoogleCalendarService;
import com.meetingnecessity.score.service.ScoringService;
import com.meetingnecessity.score.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/meetings")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class MeetingController {

    private final MeetingRepository meetingRepository;
    private final GoogleCalendarService googleCalendarService;
    private final ScoringService scoringService;
    private final UserService userService;

    public MeetingController(MeetingRepository meetingRepository, 
                             GoogleCalendarService googleCalendarService, 
                             ScoringService scoringService, 
                             UserService userService) {
        this.meetingRepository = meetingRepository;
        this.googleCalendarService = googleCalendarService;
        this.scoringService = scoringService;
        this.userService = userService;
    }

    /**
     * GET /api/meetings/sync → fetches this week's events from Google Calendar API using stored access token, saves as Meeting records
     */
    @GetMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncMeetings(@RequestParam(value = "userId", required = false) Long userId) {
        User user = (userId != null) ? userService.findById(userId).orElseGet(userService::getOrCreateDefaultUser) : userService.getOrCreateDefaultUser();
        
        List<Meeting> syncedMeetings = googleCalendarService.syncThisWeeksMeetings(user);

        // Auto-score any newly synced meetings
        for (Meeting m : syncedMeetings) {
            if (m.getScore() == null) {
                scoringService.calculateAndSaveScore(m);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("syncedCount", syncedMeetings.size());
        response.put("message", "Successfully synced " + syncedMeetings.size() + " meetings from Google Calendar.");
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/meetings/{id}/score → runs the scoring logic, calls Claude API for agenda clarity, saves Score + Suggestion
     */
    @PostMapping("/{id}/score")
    public ResponseEntity<ScoreDTO> scoreMeeting(@PathVariable("id") Long meetingId,
                                                 @RequestParam(value = "userId", required = false) Long userId) {
        User user = (userId != null) ? userService.findById(userId).orElseGet(userService::getOrCreateDefaultUser) : userService.getOrCreateDefaultUser();

        // Security: enforce user ownership check
        Optional<Meeting> meetingOpt = meetingRepository.findByIdAndUser(meetingId, user);
        if (meetingOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Meeting meeting = meetingOpt.get();
        Score score = scoringService.calculateAndSaveScore(meeting);

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

        return ResponseEntity.ok(scoreDTO);
    }

    /**
     * GET /api/meetings → returns all meetings for user
     */
    @GetMapping
    public ResponseEntity<List<MeetingDTO>> getAllMeetings(@RequestParam(value = "userId", required = false) Long userId) {
        User user = (userId != null) ? userService.findById(userId).orElseGet(userService::getOrCreateDefaultUser) : userService.getOrCreateDefaultUser();

        List<Meeting> meetings = meetingRepository.findByUser(user);
        List<MeetingDTO> dtos = meetings.stream().map(m -> {
            long durationMins = Duration.between(m.getStartTime(), m.getEndTime()).toMinutes();
            Score score = m.getScore();
            ScoreDTO scoreDTO = null;
            if (score != null) {
                Suggestion s = score.getSuggestion();
                SuggestionDTO sDTO = s != null ? new SuggestionDTO(s.getId(), s.getSuggestionText(), s.getSource()) : null;
                scoreDTO = new ScoreDTO(score.getId(), score.getNecessityScore(), score.getEstimatedCost(), score.getStatus(), score.getCalculatedAt(), sDTO);
            }
            return new MeetingDTO(m.getId(), m.getGoogleEventId(), m.getTitle(), m.getStartTime(), m.getEndTime(), m.getAttendeesCount(), m.getAgendaText(), m.isRecurring(), durationMins, scoreDTO);
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}
