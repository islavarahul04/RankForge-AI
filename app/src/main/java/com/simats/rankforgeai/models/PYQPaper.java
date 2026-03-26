package com.simats.rankforgeai.models;

import com.google.gson.annotations.SerializedName;

public class PYQPaper {
    @SerializedName("id")
    private int id;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("file")
    private String fileUrl;
    
    @SerializedName("exam_category")
    private String category;
    
    @SerializedName("year")
    private int year;
    
    @SerializedName("created_at")
    private String createdAt;

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getFileUrl() { return fileUrl; }
    public String getCategory() { return category; }
    public int getYear() { return year; }
    public String getCreatedAt() { return createdAt; }
}
