package com.op.crush;


import com.google.gson.JsonObject;
import com.op.crush.models.UserShow;
import com.op.crush.models.followmodel;
import com.op.crush.models.unfollowFind;
import com.google.gson.JsonArray;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServiceListener {

    @GET("1.1/followers/list.json")
    Call<followmodel> FollowersList(@Query("user_id") long id, @Query("cursor") long next, @Query("count") int count);

    @GET("1.1/friends/list.json")
    Call<followmodel> FollowingList(@Query("user_id") long id, @Query("cursor") long next, @Query("count") int count);

    @GET("1.1/users/show.json")
    Call<UserShow> User(@Query("user_id") long id, @Query("screen_name") String screen_name);

    @GET("1.1/statuses/home_timeline.json")
    Call<JsonArray> HomeTimeline(@Query("count") int count);

    @GET("1.1/friendships/outgoing.json")
    Call<unfollowFind> Unfollow();

    @POST("1.1/friendships/create.json")
    Call<JsonObject> CreateFollow(@Query("user_id") long id);

    @POST("1.1/friendships/destroy.json")
    Call<JsonObject> DestroyFollow(@Query("user_id") long id);

    @GET("1.1/users/lookup.json")
    Call<JsonArray> SeeUserInfo(@Query("user_id") long id);

    @GET("1.1/users/search.json")
    Call<JsonArray> SearchUser(@Query("q") String query);

    @GET("1.1/statuses/show.json")
    Call<JsonObject> getTwit(@Query("id") long id);

    @GET("1.1/followers/ids.json")
    Call<JsonObject> getid(@Query("id") long id,@Query("cursor") long next,@Query("count") int count);


}
