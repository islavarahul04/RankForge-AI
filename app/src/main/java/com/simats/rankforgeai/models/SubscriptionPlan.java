package com.simats.rankforgeai.models;

import com.google.gson.annotations.SerializedName;

public class SubscriptionPlan {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("price")
    private double price;

    @SerializedName("description")
    private String description;

    @SerializedName("duration_days")
    private int durationDays;

    @SerializedName("is_active")
    private boolean isActive;

    public SubscriptionPlan(String name, double price, String description, int durationDays, boolean isActive) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.durationDays = durationDays;
        this.isActive = isActive;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public int getDurationDays() { return durationDays; }
    public boolean isActive() { return isActive; }

    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
    public void setDurationDays(int durationDays) { this.durationDays = durationDays; }
    public void setActive(boolean active) { isActive = active; }
}
