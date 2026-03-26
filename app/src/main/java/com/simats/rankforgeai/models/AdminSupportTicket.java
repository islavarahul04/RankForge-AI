package com.simats.rankforgeai.models;

import com.google.gson.annotations.SerializedName;

public class AdminSupportTicket {
    @SerializedName("id")
    private int id;

    @SerializedName("user_email")
    private String userEmail;

    @SerializedName("user_name")
    private String userName;

    @SerializedName("message")
    private String message;

    @SerializedName("admin_reply")
    private String adminReply;

    @SerializedName("replied_at")
    private String repliedAt;

    @SerializedName("is_resolved")
    private boolean isResolved;

    @SerializedName("created_at")
    private String createdAt;

    public int getId() { return id; }
    public String getUserEmail() { return userEmail; }
    public String getUserName() { return userName; }
    public String getMessage() { return message; }
    public String getAdminReply() { return adminReply; }
    public String getRepliedAt() { return repliedAt; }
    public boolean isResolved() { return isResolved; }
    public String getCreatedAt() { return createdAt; }
}
