package com.simats.rankforgeai.models;

public class AuthResponse {
    private UserData user;
    private String access;
    private String refresh;
    private String error;

    public UserData getUser() { return user; }
    public String getAccess() { return access; }
    public String getRefresh() { return refresh; }
    public String getError() { return error; }

    public static class UserData {
        private int id;
        private String email;
        private String full_name;
        private String target_exam;
        private boolean is_premium;
        private String premium_plan;
        
        public int getId() { return id; }
        public String getEmail() { return email; }
        public String getFullName() { return full_name; }
        public String getTargetExam() { return target_exam; }
        public boolean isPremium() { return is_premium; }
        public String getPremiumPlan() { return premium_plan; }
    }
}
