package com.opteam.tools.Room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProgressDao {

    @Insert
    void insert(ProgressState state);

    @Update
    void update(ProgressState state);

    @Delete
    void delete(ProgressState state);


    @Query("SELECT * FROM progress")
    LiveData<List<ProgressState>> getStates();

}
