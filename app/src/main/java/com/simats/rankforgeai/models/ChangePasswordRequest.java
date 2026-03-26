package com.simats.rankforgeai.models;

public class ChangePasswordRequest {
    private String current_password;
    private String new_password;

    public ChangePasswordRequest(String current_password, String new_password) {
        this.current_password = current_password;
        this.new_password = new_password;
    }

    public String getCurrentPassword() {
        return current_password;
    }

    public void setCurrentPassword(String current_password) {
        this.current_password = current_password;
    }

    public String getNewPassword() {
        return new_password;
    }

    public void setNewPassword(String new_password) {
        this.new_password = new_password;
    }
}
