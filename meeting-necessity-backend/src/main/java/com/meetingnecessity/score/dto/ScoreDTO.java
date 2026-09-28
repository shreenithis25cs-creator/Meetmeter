package com.meetingnecessity.score.dto;

import com.meetingnecessity.score.model.enums.MeetingStatus;
import java.time.LocalDateTime;

public class ScoreDTO {
    private Long id;
    private int necessityScore;
    private double estimatedCost;
    private MeetingStatus status;
    private LocalDateTime calculatedAt;
    private SuggestionDTO suggestion;

    public ScoreDTO() {
    }

    public ScoreDTO(Long id, int necessityScore, double estimatedCost, MeetingStatus status, 
                    LocalDateTime calculatedAt, SuggestionDTO suggestion) {
        this.id = id;
        this.necessityScore = necessityScore;
        this.estimatedCost = estimatedCost;
        this.status = status;
        this.calculatedAt = calculatedAt;
        this.suggestion = suggestion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public SuggestionDTO getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(SuggestionDTO suggestion) {
        this.suggestion = suggestion;
    }
}
