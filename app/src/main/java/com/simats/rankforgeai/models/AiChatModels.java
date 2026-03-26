package com.simats.rankforgeai.models;

import java.util.List;

public class AiChatModels {
    
    public static class ChatRequest {
        private String message;

        public ChatRequest(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class ChatMessage {
        private int id;
        private String message;
        private boolean is_user;
        private String created_at;

        public int getId() { return id; }
        public String getMessage() { return message; }
        public boolean isUser() { return is_user; }
        public String getCreatedAt() { return created_at; }
    }
}
