package com.simats.rankforgeai.models;

public class UserProfile {
    private int id;
    private String email;
    private String full_name;
    private String phone_number;
    private String dob;
    private String city;
    private String gender;
    private String target_exam;
    private String profile_picture;
    private String referral_code;
    private boolean is_premium;
    private String premium_plan;
    
    private int tests_attempted;
    private double average_accuracy;
    
    // Settings Preferences
    private boolean push_notifications;
    private boolean study_reminders;
    private boolean test_alerts;
    private boolean community_updates;

    public UserProfile(String full_name, String phone_number, String dob, String city, String gender, String target_exam) {
        this.full_name = full_name;
        this.phone_number = phone_number;
        this.dob = dob;
        this.city = city;
        this.gender = gender;
        this.target_exam = target_exam;
        // Default Settings
        this.push_notifications = false;
        this.study_reminders = false;
        this.test_alerts = false;
        this.community_updates = true;
    }

    public int getId() { return id; }
    public String getEmail() { return email; }
    public String getFullName() { return full_name; }
    public String getPhoneNumber() { return phone_number; }
    public String getDob() { return dob; }
    public String getCity() { return city; }
    public String getGender() { return gender; }
    public String getTargetExam() { return target_exam; }
    public String getProfilePicture() { return profile_picture; }
    public String getReferralCode() { return referral_code; }
    
    public int getTestsAttempted() { return tests_attempted; }
    public double getAverageAccuracy() { return average_accuracy; }

    public boolean getPushNotifications() { return push_notifications; }
    public void setPushNotifications(boolean push_notifications) { this.push_notifications = push_notifications; }

    public boolean getStudyReminders() { return study_reminders; }
    public void setStudyReminders(boolean study_reminders) { this.study_reminders = study_reminders; }

    public boolean getTestAlerts() { return test_alerts; }
    public void setTestAlerts(boolean test_alerts) { this.test_alerts = test_alerts; }

    public boolean getCommunityUpdates() { return community_updates; }
    public void setCommunityUpdates(boolean community_updates) { this.community_updates = community_updates; }

    public boolean isPremium() { return is_premium; }
    public void setPremium(boolean premium) { is_premium = premium; }

    public String getPremiumPlan() { return premium_plan; }
    public void setPremiumPlan(String premiumPlan) { premium_plan = premiumPlan; }
}
