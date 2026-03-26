package com.simats.rankforgeai.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class StudySubject {
    @SerializedName("id")
    private int id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("icon_name")
    private String icon_name;
    
    @SerializedName("icon_bg_drawable")
    private String icon_bg_drawable;
    
    @SerializedName("progress_drawable")
    private String progress_drawable;
    
    @SerializedName("order")
    private int order;
    
    @SerializedName("topics")
    private List<StudyTopic> topics;

    public int getId() { return id; }
    public String getName() { return name; }
    public String getIconName() { return icon_name; }
    public String getIconBgDrawable() { return icon_bg_drawable; }
    public String getProgressDrawable() { return progress_drawable; }
    public int getOrder() { return order; }
    public List<StudyTopic> getTopics() { return topics; }
}
