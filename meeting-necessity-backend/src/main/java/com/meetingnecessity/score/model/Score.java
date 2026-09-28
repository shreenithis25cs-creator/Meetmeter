package com.meetingnecessity.score.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meetingnecessity.score.model.enums.MeetingStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scores")
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id", nullable = false, unique = true)
    @JsonIgnore
    private Meeting meeting;

    @Column(name = "necessity_score", nullable = false)
    private int necessityScore;

    @Column(name = "estimated_cost", nullable = false)
    private double estimatedCost;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeetingStatus status;

    @Column(name = "calculated_at", nullable = false)
    private LocalDateTime calculatedAt;

    @OneToOne(mappedBy = "score", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Suggestion suggestion;

    public Score() {
    }

    public Score(Meeting meeting, int necessityScore, double estimatedCost, MeetingStatus status) {
        this.meeting = meeting;
        this.necessityScore = necessityScore;
        this.estimatedCost = estimatedCost;
        this.status = status;
        this.calculatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (calculatedAt == null) {
            calculatedAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public int getNecessityScore() {
        return necessityScore;
    }

    public void setNecessityScore(int necessityScore) {
        this.necessityScore = necessityScore;
    }

    public double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public MeetingStatus getStatus() {
        return status;
    }

    public void setStatus(MeetingStatus status) {
        this.status = status;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }

    public Suggestion getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(Suggestion suggestion) {
        this.suggestion = suggestion;
    }
}
