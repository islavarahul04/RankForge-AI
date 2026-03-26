package com.simats.rankforgeai.models;

import com.google.gson.annotations.SerializedName;

public class StudyTopic {
    @SerializedName("id")
    private int id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("order")
    private int order;
    
    @SerializedName("is_completed")
    private boolean is_completed;

    @SerializedName("theory")
    private String theory;

    @SerializedName("formulas")
    private String formulas;

    @SerializedName("examples")
    private java.util.List<ExampleProblem> examples;

    public int getId() { return id; }
    public String getName() { return name; }
    public int getOrder() { return order; }
    public boolean getIs_completed() { return is_completed; }
    public String getTheory() { return theory; }
    public String getFormulas() { return formulas; }
    public java.util.List<ExampleProblem> getExamples() { return examples; }

    public void setTheory(String theory) { this.theory = theory; }
    public void setFormulas(String formulas) { this.formulas = formulas; }
    public void setExamples(java.util.List<ExampleProblem> examples) { this.examples = examples; }

    public void setCompleted(boolean completed) {
        this.is_completed = completed;
    }

    public static class ExampleProblem {
        @SerializedName("question")
        private String question;
        @SerializedName("solution")
        private String solution;

        public String getQuestion() { return question; }
        public String getSolution() { return solution; }

        public void setQuestion(String question) { this.question = question; }
        public void setSolution(String solution) { this.solution = solution; }
    }
}
