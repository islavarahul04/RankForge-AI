package com.simats.rankforgeai.models;

public class Notification {
    private int id;
    private String title;
    private String message;
    private String category;
    private boolean is_read;
    private String created_at;

    public Notification(String title, String message, String category, boolean is_read) {
        this.title = title;
        this.message = message;
        this.category = category;
        this.is_read = is_read;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getCategory() { return category; }
    public boolean isRead() { return is_read; }
    public String getCreatedAt() { return created_at; }
}
