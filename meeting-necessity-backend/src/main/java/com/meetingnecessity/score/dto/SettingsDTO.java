package com.meetingnecessity.score.dto;

public class SettingsDTO {
    private double costPerPersonPerHour;
    private int lowScoreThreshold;
    private int highScoreThreshold;

    public SettingsDTO() {
    }

    public SettingsDTO(double costPerPersonPerHour, int lowScoreThreshold, int highScoreThreshold) {
        this.costPerPersonPerHour = costPerPersonPerHour;
        this.lowScoreThreshold = lowScoreThreshold;
        this.highScoreThreshold = highScoreThreshold;
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
