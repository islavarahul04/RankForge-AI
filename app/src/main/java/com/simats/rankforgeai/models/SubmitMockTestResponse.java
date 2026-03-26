package com.simats.rankforgeai.models;

public class SubmitMockTestResponse {
    private String message;
    private int score;
    private boolean is_completed;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public boolean getIs_completed() { return is_completed; }
    public void setIs_completed(boolean is_completed) { this.is_completed = is_completed; }
}
