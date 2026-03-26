package com.simats.rankforgeai.models;

public class SubscribeRequest {
    private int plan_id;

    public SubscribeRequest(int plan_id) {
        this.plan_id = plan_id;
    }

    public int getPlanId() {
        return plan_id;
    }

    public void setPlanId(int plan_id) {
        this.plan_id = plan_id;
    }
}
