package com.opteam.tools.models;

import android.content.ContentValues;

public class SuggestUser {
    public static final String Key_ID = "Id";
    public static final String KEY_NAME = "name";
    public static final String KEY_SCREEN = "screen_name";
    public static final String KEY_IMAGE = "Uri_image";


    private long id;

    private String name;

    private String profilePictureUrl;


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
        values.put(KEY_SCREEN,getScreenName());
        if (getProfilePictureUrl() == null) {
            values.put(KEY_IMAGE, "non");
        } else {
            values.put(KEY_IMAGE, getProfilePictureUrl());
        }


        return values;


    }
}
