package com.op.crush.Room;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "progress")
public class ProgressState {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int state;

    public ProgressState(int state) {
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
