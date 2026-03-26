package com.simats.rankforgeai.models;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class CreateMockTestRequest {
    @SerializedName("name")
    private String name;
    
    @SerializedName("is_free")
    private boolean is_free;
    
    @SerializedName("questions")
    private List<Question> questions;

    public CreateMockTestRequest(String name, boolean is_free, List<Question> questions) {
        this.name = name;
        this.is_free = is_free;
        this.questions = questions;
    }
}
