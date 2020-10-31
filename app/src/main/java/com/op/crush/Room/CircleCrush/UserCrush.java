package com.op.crush.Room.CircleCrush;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "UserCrush")
public class UserCrush {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private long user_id;

    public UserCrush(long user_id) {
        this.user_id = user_id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public long getUser_id() {
        return user_id;
    }

}
