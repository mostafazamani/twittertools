package com.opteam.tools.Room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.opteam.tools.Room.ProgressState;
import com.opteam.tools.Room.ProgressStateFollower;

import java.util.List;

@Dao
public interface ProgressDaoFollower {

    @Insert
    void insert(ProgressStateFollower state);

    @Update
    void update(ProgressStateFollower state);

    @Delete
    void delete(ProgressStateFollower state);


    @Query("SELECT * FROM progressfollower")
    LiveData<List<ProgressStateFollower>> getStates();

}
