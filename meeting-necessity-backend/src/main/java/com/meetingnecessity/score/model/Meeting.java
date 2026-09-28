package com.meetingnecessity.score.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "meetings")
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "google_event_id")
    private String googleEventId;

    @Column(nullable = false)
    private String title;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "attendees_count", nullable = false)
    private int attendeesCount;

    @Column(name = "agenda_text", columnDefinition = "TEXT")
    private String agendaText;

    @Column(name = "is_recurring", nullable = false)
    private boolean isRecurring;

    @OneToOne(mappedBy = "meeting", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Score score;

    public Meeting() {
    }

    public Meeting(User user, String googleEventId, String title, LocalDateTime startTime, 
                   LocalDateTime endTime, int attendeesCount, String agendaText, boolean isRecurring) {
        this.user = user;
        this.googleEventId = googleEventId;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.attendeesCount = attendeesCount;
        this.agendaText = agendaText;
        this.isRecurring = isRecurring;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }
}
