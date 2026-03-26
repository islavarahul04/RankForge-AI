package com.simats.rankforgeai.models;

import java.util.List;

public class AdminTestDetailsResponse {
    private String test_name;
    private List<AdminUserTestResult> results;

    public String getTestName() { return test_name; }
    public List<AdminUserTestResult> getResults() { return results; }

    public static class AdminUserTestResult {
        private String userName;
        private String email;
        private int score;
        private boolean completed;
        private String date;

        public String getUserName() { return userName; }
        public String getEmail() { return email; }
        public int getScore() { return score; }
        public boolean isCompleted() { return completed; }
        public String getDate() { return date; }
    }
}
