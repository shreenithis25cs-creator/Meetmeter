package com.meetingnecessity.score.dto;

import java.util.List;

public class DashboardResponseDTO {
    private double totalCost;
    private double flaggedMeetingsCost;
    private double hoursCouldBeSaved;
    private int totalMeetingsCount;
    private int couldBeEmailCount;
    private String dateRange;
    private SettingsDTO settings;
    private List<MeetingDTO> meetings;

    public DashboardResponseDTO() {
    }

    public DashboardResponseDTO(double totalCost, double flaggedMeetingsCost, double hoursCouldBeSaved, 
                                int totalMeetingsCount, int couldBeEmailCount, String dateRange, 
                                SettingsDTO settings, List<MeetingDTO> meetings) {
        this.totalCost = totalCost;
        this.flaggedMeetingsCost = flaggedMeetingsCost;
        this.hoursCouldBeSaved = hoursCouldBeSaved;
        this.totalMeetingsCount = totalMeetingsCount;
        this.couldBeEmailCount = couldBeEmailCount;
        this.dateRange = dateRange;
        this.settings = settings;
        this.meetings = meetings;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double getFlaggedMeetingsCost() {
        return flaggedMeetingsCost;
    }

    public void setFlaggedMeetingsCost(double flaggedMeetingsCost) {
        this.flaggedMeetingsCost = flaggedMeetingsCost;
    }

    public double getHoursCouldBeSaved() {
        return hoursCouldBeSaved;
    }

    public void setHoursCouldBeSaved(double hoursCouldBeSaved) {
        this.hoursCouldBeSaved = hoursCouldBeSaved;
    }

    public int getTotalMeetingsCount() {
        return totalMeetingsCount;
    }

    public void setTotalMeetingsCount(int totalMeetingsCount) {
        this.totalMeetingsCount = totalMeetingsCount;
    }

    public int getCouldBeEmailCount() {
        return couldBeEmailCount;
    }

    public void setCouldBeEmailCount(int couldBeEmailCount) {
        this.couldBeEmailCount = couldBeEmailCount;
    }

    public String getDateRange() {
        return dateRange;
    }

    public void setDateRange(String dateRange) {
        this.dateRange = dateRange;
    }

    public SettingsDTO getSettings() {
        return settings;
    }

    public void setSettings(SettingsDTO settings) {
        this.settings = settings;
    }

    public List<MeetingDTO> getMeetings() {
        return meetings;
    }

    public void setMeetings(List<MeetingDTO> meetings) {
        this.meetings = meetings;
    }
}
