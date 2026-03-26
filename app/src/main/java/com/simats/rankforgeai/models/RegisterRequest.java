package com.simats.rankforgeai.models;

public class RegisterRequest {
    private String email;
    private String password;
    private String full_name;

    public RegisterRequest(String email, String password, String full_name) {
        this.email = email;
        this.password = password;
        this.full_name = full_name;
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getFullName() { return full_name; }
}
