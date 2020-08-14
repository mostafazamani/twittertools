package com.example.crush.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class unfollowFind {

    @SerializedName("ids")
    @Expose
    private List<Long> id;


    public List<Long> getId() {
        return id;
    }

    public void setId(List<Long> id) {
        this.id = id;
    }
}
