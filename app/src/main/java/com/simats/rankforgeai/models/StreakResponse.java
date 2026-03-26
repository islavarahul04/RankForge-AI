package com.simats.rankforgeai.models;

import java.util.List;

public class StreakResponse {
    private int current_streak;
    private int longest_streak;
    private List<String> checked_in_dates;

    public int getCurrentStreak() {
        return current_streak;
    }

    public int getLongestStreak() {
        return longest_streak;
    }

    public List<String> getCheckedInDates() {
        return checked_in_dates;
    }
}
