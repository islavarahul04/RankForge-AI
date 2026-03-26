package com.simats.rankforgeai.models;
import com.google.gson.annotations.SerializedName;

public class Question {
    private int id;
    @SerializedName(value = "question_text", alternate = {"text", "question"})
    private String question_text;
    
    @SerializedName(value = "option1", alternate = {"a", "option_a", "option_1"})
    private String option1;
    
    @SerializedName(value = "option2", alternate = {"b", "option_b", "option_2"})
    private String option2;
    
    @SerializedName(value = "option3", alternate = {"c", "option_c", "option_3"})
    private String option3;
    
    @SerializedName(value = "option4", alternate = {"d", "option_d", "option_4"})
    private String option4;
    
    @SerializedName(value = "correct_option", alternate = {"correct", "answer"})
    private int correct_option;
    
    private String section;
    private int order;

    public Question(String question_text, String option1, String option2, String option3, String option4, int correct_option, String section, int order) {
        this.question_text = question_text;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.correct_option = correct_option;
        this.section = section;
        this.order = order;
    }

    public int getId() { return id; }
    public String getQuestionText() { return question_text; }
    public String getOption1() { return option1; }
    public String getOption2() { return option2; }
    public String getOption3() { return option3; }
    public String getOption4() { return option4; }
    public int getCorrectOption() { return correct_option; }
    public String getSection() { return section; }
    public int getOrder() { return order; }
}
