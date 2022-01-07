package com.opteam.tools.models;

import android.content.ContentValues;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class follow {

    public static final String NUMBER = "num";
    public static final String Key_ID = "Id";
    public static final String KEY_NAME = "Item_text";
    public static final String KEY_IMAGE = "Uri_image";


    @SerializedName("id")
    @Expose
    private long id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("profile_image_url_https")
    @Expose
    private String profilePictureUrl;

    @SerializedName("screen_name")
    @Expose
    private String screenName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        values.put(Key_ID, getId());
        values.put(KEY_NAME, getName());
        if (getProfilePictureUrl() == null) {
            values.put(KEY_IMAGE, "non");
        } else {
            values.put(KEY_IMAGE, getProfilePictureUrl());
        }


        return values;


    }


}
