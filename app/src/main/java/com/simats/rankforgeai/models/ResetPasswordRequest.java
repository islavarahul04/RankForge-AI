package com.simats.rankforgeai.models;

public class ResetPasswordRequest {
    private String new_password;

    public ResetPasswordRequest(String new_password) {
        this.new_password = new_password;
    }

    public String getNewPassword() { return new_password; }
    public void setNewPassword(String new_password) { this.new_password = new_password; }
}
