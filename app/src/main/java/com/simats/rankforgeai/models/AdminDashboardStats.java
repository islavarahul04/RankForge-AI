package com.simats.rankforgeai.models;

public class AdminDashboardStats {
    private int total_users;
    private String total_users_percentage;
    private int active_today;
    private String active_today_percentage;
    private int revenue;
    private String revenue_percentage;
    private int tests_taken;
    private String tests_taken_percentage;
    private java.util.List<RecentActivity> recent_activity;

    public int getTotalUsers() { return total_users; }
    public String getTotalUsersPercentage() { return total_users_percentage; }
    
    public int getActiveToday() { return active_today; }
    public String getActiveTodayPercentage() { return active_today_percentage; }

    public int getRevenue() { return revenue; }
    public String getRevenuePercentage() { return revenue_percentage; }

    public int getTestsTaken() { return tests_taken; }
    public String getTestsTakenPercentage() { return tests_taken_percentage; }

    public java.util.List<RecentActivity> getRecentActivity() { return recent_activity; }

    public static class RecentActivity {
        private String title;
        private String message;
        private String category;
        private String created_at;

        public String getTitle() { return title; }
        public String getMessage() { return message; }
        public String getCategory() { return category; }
        public String getCreatedAt() { return created_at; }
    }
}
