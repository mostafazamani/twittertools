package com.opteam.tools.models;

public class UserCrushSearch {

    private long id;
    private String name;
    private String screen_name;
    private String profile_pic;

    public UserCrushSearch(long id, String name, String screen_name, String profile_pic) {
        this.id = id;
        this.name = name;
        this.screen_name = screen_name;
        this.profile_pic = profile_pic;
    }


    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public String getProfile_pic() {
        return profile_pic;
    }
}
