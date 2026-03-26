package com.simats.rankforgeai.models;

public class UpdateProgressRequest {
    private int topic_id;
    private boolean is_completed;

    public UpdateProgressRequest(int topic_id, boolean is_completed) {
        this.topic_id = topic_id;
        this.is_completed = is_completed;
    }
}
