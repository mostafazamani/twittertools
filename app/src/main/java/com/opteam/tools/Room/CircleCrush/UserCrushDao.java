package com.opteam.tools.Room.CircleCrush;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserCrushDao {

    @Insert
    void insert(UserCrush state);

    @Update
    void update(UserCrush state);

    @Delete
    void delete(UserCrush state);

    @Query("DELETE FROM UserCrush")
    void deleteAll();


    @Query("SELECT * FROM UserCrush")
    List<UserCrush> getUserCrush();

}
