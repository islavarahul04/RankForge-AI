package com.simats.rankforgeai.models;

import com.google.gson.annotations.SerializedName;

public class MockTest {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    
    @SerializedName("is_free")
    private boolean is_free;
    
    @SerializedName("order")
    private int order;
    
    @SerializedName("is_completed")
    private boolean is_completed;
    
    @SerializedName("highest_score")
    private int highest_score;

    @SerializedName("latest_score")
    private int latest_score;

    @SerializedName("question_count")
    private int question_count;
    
    @SerializedName("is_unlocked_manually")
    private boolean is_unlocked_manually;
    
    @SerializedName("questions")
    private java.util.List<Question> questions;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean getIs_free() { return is_free; }
    public void setIs_free(boolean is_free) { this.is_free = is_free; }

    public boolean isUnlockedManually() { return is_unlocked_manually; }
    public void setUnlockedManually(boolean unlockedManually) { this.is_unlocked_manually = unlockedManually; }

    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }

    public boolean getIs_completed() { return is_completed; }
    public void setIs_completed(boolean is_completed) { this.is_completed = is_completed; }

    public int getHighest_score() { return highest_score; }
    public void setHighest_score(int highest_score) { this.highest_score = highest_score; }

    public int getLatest_score() { return latest_score; }
    public void setLatest_score(int latest_score) { this.latest_score = latest_score; }

    public int getQuestionCount() { return question_count; }
    public void setQuestionCount(int question_count) { this.question_count = question_count; }

    public java.util.List<Question> getQuestions() { return questions; }
    public void setQuestions(java.util.List<Question> questions) { this.questions = questions; }
}
