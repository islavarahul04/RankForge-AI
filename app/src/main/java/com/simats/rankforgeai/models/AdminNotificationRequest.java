package com.simats.rankforgeai.models;

public class AdminNotificationRequest {
    private String title;
    private String message;
    private String category;

    public AdminNotificationRequest(String title, String message, String category) {
        this.title = title;
        this.message = message;
        this.category = category;
    }

    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getCategory() { return category; }
}
