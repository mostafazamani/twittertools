package com.op.crush.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class followmodel {

    @SerializedName("next_cursor_str")
    @Expose
    private String nextCursorStr;

    @SerializedName("next_cursor")
    @Expose
    private long nextCursor;

    @SerializedName("previous_cursor")
    @Expose
    private long previousCursor;

    @SerializedName("previous_cursor_str")
    @Expose
    private String previousCursorStr;

    @SerializedName("users")
    @Expose
    private List<follow> results = new ArrayList<>();

    public long getPreviousCursor() {
        return previousCursor;
    }

    public void setPreviousCursor(Integer previousCursor) {
        this.previousCursor = previousCursor;
    }

    public String getPreviousCursorStr() {
        return previousCursorStr;
    }

    public void setPreviousCursorStr(String previousCursorStr) {
        this.previousCursorStr = previousCursorStr;
    }
    public void setNextCursor(long nextCursor) {
        this.nextCursor = nextCursor;
    }


    public long getNextCursor() { return nextCursor; }

    public String getNextCursorStr() {
        return nextCursorStr;
    }

    public void setNextCursorStr(String nextCursorStr) {
        this.nextCursorStr = nextCursorStr;
    }

    public List<follow> getResults() {
        return results;
    }

    public void setResults(List<follow> results) {
        this.results = results;
    }
}
