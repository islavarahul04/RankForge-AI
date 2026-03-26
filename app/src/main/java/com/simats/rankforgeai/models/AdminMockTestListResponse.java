package com.simats.rankforgeai.models;

import java.util.List;

public class AdminMockTestListResponse {
    private int total_count;
    private List<AdminMockTest> tests;

    public int getTotalCount() { return total_count; }
    public List<AdminMockTest> getTests() { return tests; }

    public static class AdminMockTest {
        private int id;
        private String name;
        private int question_count;
        private boolean is_free;

        public int getId() { return id; }
        public String getName() { return name; }
        public int getQuestionCount() { return question_count; }
        public boolean isFree() { return is_free; }
    }
}
