package com.simats.rankforgeai.models;

public class SubmitMockTestRequest {
    private int test_id;
    private int score;
    private int total_questions;
    private int correct_count;
    private int incorrect_count;
    private int eng_score;
    private int quant_score;
    private int reason_score;
    private int gk_score;
    private int[] selected_answers;

    public SubmitMockTestRequest(int test_id, int score, int total_questions, int correct_count, int incorrect_count, int eng_score, int quant_score, int reason_score, int gk_score, int[] selected_answers) {
        this.test_id = test_id;
        this.score = score;
        this.total_questions = total_questions;
        this.correct_count = correct_count;
        this.incorrect_count = incorrect_count;
        this.eng_score = eng_score;
        this.quant_score = quant_score;
        this.reason_score = reason_score;
        this.gk_score = gk_score;
        this.selected_answers = selected_answers;
    }

    public int getTest_id() { return test_id; }
    public void setTest_id(int test_id) { this.test_id = test_id; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getTotal_questions() { return total_questions; }
    public int getCorrect_count() { return correct_count; }
    public int getIncorrect_count() { return incorrect_count; }
    public int getEng_score() { return eng_score; }
    public int getQuant_score() { return quant_score; }
    public int getReason_score() { return reason_score; }
    public int getGk_score() { return gk_score; }
    public int[] getSelected_answers() { return selected_answers; }
}
