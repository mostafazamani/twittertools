package com.example.crush;


import com.example.crush.models.UserShow;
import com.example.crush.models.followingmodel;
import com.google.gson.JsonArray;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServiceListener {

    @GET("1.1/followers/list.json")
    Call<followingmodel> FollowersList(@Query("user_id") long id, @Query("cursor") long next, @Query("count") int count);

    @GET("1.1/users/show.json")
    Call<UserShow> User(@Query("user_id") long id, @Query("screen_name") String screen_name);

    @GET("1.1/statuses/home_timeline.json")
    Call<JsonArray> HomeTimeline(@Query("count") int count);

}
