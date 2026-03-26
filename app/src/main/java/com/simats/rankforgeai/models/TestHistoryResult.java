package com.simats.rankforgeai.models;

import com.google.gson.annotations.SerializedName;

public class TestHistoryResult {
    private int id;
    
    @SerializedName("test_id")
    private int test_id;
    
    @SerializedName("test_name")
    private String test_name;
    
    private int score;
    
    @SerializedName("total_questions")
    private int total_questions;
    
    @SerializedName("correct_count")
    private int correct_count;
    
    @SerializedName("incorrect_count")
    private int incorrect_count;
    
    @SerializedName("eng_score")
    private int eng_score;
    
    @SerializedName("quant_score")
    private int quant_score;
    
    @SerializedName("reason_score")
    private int reason_score;
    
    @SerializedName("gk_score")
    private int gk_score;
    
    @SerializedName("selected_answers")
    private int[] selected_answers;
    
    @SerializedName("is_completed")
    private boolean is_completed;
    
    @SerializedName("created_at")
    private String created_at;

    public int getId() { return id; }
    public int getTestId() { return test_id; }
    public String getTestName() { return test_name; }
    public int getScore() { return score; }
    public int getTotalQuestions() { return total_questions; }
    public int getCorrectCount() { return correct_count; }
    public int getIncorrectCount() { return incorrect_count; }
    public int getEngScore() { return eng_score; }
    public int getQuantScore() { return quant_score; }
    public int getReasonScore() { return reason_score; }
    public int getGkScore() { return gk_score; }
    public int[] getSelectedAnswers() { return selected_answers; }
    public boolean isCompleted() { return is_completed; }
    public String getCreatedAt() { return created_at; }
}
