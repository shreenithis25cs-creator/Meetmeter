package com.meetingnecessity.score.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "user_settings")
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnore
    private User user;

    @Column(name = "cost_per_person_per_hour", nullable = false)
    private double costPerPersonPerHour = 500.0;

    @Column(name = "low_score_threshold", nullable = false)
    private int lowScoreThreshold = 40;

    @Column(name = "high_score_threshold", nullable = false)
    private int highScoreThreshold = 70;

    public Settings() {
    }

    public Settings(User user) {
        this.user = user;
        this.costPerPersonPerHour = 500.0;
        this.lowScoreThreshold = 40;
        this.highScoreThreshold = 70;
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

    public double getCostPerPersonPerHour() {
        return costPerPersonPerHour;
    }

    public void setCostPerPersonPerHour(double costPerPersonPerHour) {
        this.costPerPersonPerHour = costPerPersonPerHour;
    }

    public int getLowScoreThreshold() {
        return lowScoreThreshold;
    }

    public void setLowScoreThreshold(int lowScoreThreshold) {
        this.lowScoreThreshold = lowScoreThreshold;
    }

    public int getHighScoreThreshold() {
        return highScoreThreshold;
    }

    public void setHighScoreThreshold(int highScoreThreshold) {
        this.highScoreThreshold = highScoreThreshold;
    }
}
