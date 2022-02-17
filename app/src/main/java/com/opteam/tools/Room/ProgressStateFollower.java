package com.opteam.tools.Room;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "progress")
public class ProgressStateFollower {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int state;

    public ProgressStateFollower(int state) {
        this.state = state;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getState() {
        return state;
    }
}
