package com.simats.rankforgeai.models;

public class ExamCategory {
    private int id;
    private String name;
    private String tag_bg_color;
    private String icon_bg_color;
    private String icon_tint;
    private String icon_name;
    private boolean is_locked;

    public int getId() { return id; }
    public String getName() { return name; }
    public String getTagBgColor() { return tag_bg_color; }
    public String getIconBgColor() { return icon_bg_color; }
    public String getIconTint() { return icon_tint; }
    public String getIconName() { return icon_name; }
    public boolean isLocked() { return is_locked; }
}
