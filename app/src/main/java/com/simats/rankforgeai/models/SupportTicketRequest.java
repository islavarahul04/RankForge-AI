package com.simats.rankforgeai.models;

public class SupportTicketRequest {
    private String message;

    private String admin_reply;
    private String replied_at;
    private boolean is_resolved;
    private String created_at;

    public SupportTicketRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAdminReply() {
        return admin_reply;
    }

    public void setAdminReply(String adminReply) {
        this.admin_reply = adminReply;
    }

    public String getRepliedAt() {
        return replied_at;
    }

    public void setRepliedAt(String repliedAt) {
        this.replied_at = repliedAt;
    }

    public boolean isResolved() {
        return is_resolved;
    }

    public void setResolved(boolean resolved) {
        is_resolved = resolved;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(String createdAt) {
        this.created_at = createdAt;
    }
}
