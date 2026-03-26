package com.simats.rankforgeai.models;

public class AdminUser {
    private String email;
    private String full_name;
    private boolean is_active;
    private boolean is_premium;
    private String premium_plan;
    private boolean isDuplicate;

    public AdminUser() {}

    public String getEmail() {
        return email != null ? email : "";
    }

    public String getFullName() {
        return full_name != null ? full_name : "RankForge User";
    }

    public boolean isActive() {
        return is_active;
    }

    public boolean isPremium() {
        return is_premium;
    }

    public String getPremiumPlan() {
        return premium_plan != null ? premium_plan : "";
    }

    public boolean isDuplicate() {
        return isDuplicate;
    }

    public void setDuplicate(boolean duplicate) {
        isDuplicate = duplicate;
    }

    // Generate dynamic initial strings based on full name for the Avatar circle (e.g. JD)
    public String getInitials() {
        String name = getFullName().trim();
        if (name.isEmpty()) return "U";
        
        String[] parts = name.split(" ");
        if (parts.length == 1) {
            return String.valueOf(parts[0].charAt(0)).toUpperCase();
        } else {
            return (String.valueOf(parts[0].charAt(0)) + parts[parts.length - 1].charAt(0)).toUpperCase();
        }
    }
}
