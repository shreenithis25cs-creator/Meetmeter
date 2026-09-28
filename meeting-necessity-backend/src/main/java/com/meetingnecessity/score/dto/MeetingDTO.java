package com.meetingnecessity.score.dto;

import java.time.LocalDateTime;

public class MeetingDTO {
    private Long id;
    private String googleEventId;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int attendeesCount;
    private String agendaText;
    private boolean isRecurring;
    private long durationMinutes;
    private ScoreDTO score;

    public MeetingDTO() {
    }

    public MeetingDTO(Long id, String googleEventId, String title, LocalDateTime startTime, 
                      LocalDateTime endTime, int attendeesCount, String agendaText, 
                      boolean isRecurring, long durationMinutes, ScoreDTO score) {
        this.id = id;
        this.googleEventId = googleEventId;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.attendeesCount = attendeesCount;
        this.agendaText = agendaText;
        this.isRecurring = isRecurring;
        this.durationMinutes = durationMinutes;
        this.score = score;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGoogleEventId() {
        return googleEventId;
    }

    public void setGoogleEventId(String googleEventId) {
        this.googleEventId = googleEventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public int getAttendeesCount() {
        return attendeesCount;
    }

    public void setAttendeesCount(int attendeesCount) {
        this.attendeesCount = attendeesCount;
    }

    public String getAgendaText() {
        return agendaText;
    }

    public void setAgendaText(String agendaText) {
        this.agendaText = agendaText;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }

    public long getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(long durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public ScoreDTO getScore() {
        return score;
    }

    public void setScore(ScoreDTO score) {
        this.score = score;
    }
}
