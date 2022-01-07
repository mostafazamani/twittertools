package com.opteam.tools.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserShow {


    @SerializedName("name")
    @Expose
    private String profile_name;

    @SerializedName("screen_name")
    @Expose
    private String screen_name;

    @SerializedName("followers_count")
    @Expose
    private int followers_count;


    @SerializedName("friends_count")
    @Expose
    private int followings_count;

    @SerializedName("profile_image_url")
    @Expose
    private String profile_image_url;

    @SerializedName("profile_banner_url")
    @Expose
    private String profile_banner_url;

    @SerializedName("statuses_count")
    @Expose
    private int statuses_count;

    public int getStatuses_count() {
        return statuses_count;
    }

    public void setStatuses_count(int statuses_count) {
        this.statuses_count = statuses_count;
    }

    public String getProfile_name() {
        return profile_name;
    }

    public void setProfile_name(String profile_name) {
        this.profile_name = profile_name;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public int getFollowers_count() {
        return followers_count;
    }

    public void setFollowers_count(int followers_count) {
        this.followers_count = followers_count;
    }

    public int getFollowings_count() {
        return followings_count;
    }

    public void setFollowings_count(int followings_count) {
        this.followings_count = followings_count;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }

    public String getProfile_banner_url() {
        return profile_banner_url;
    }

    public void setProfile_banner_url(String profile_banner_url) {
        this.profile_banner_url = profile_banner_url;
    }
}
