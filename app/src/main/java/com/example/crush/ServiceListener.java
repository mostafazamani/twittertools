package com.example.crush;



import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServiceListener {

    @GET("1.1/followers/list.json")
    Call<followingmodel> list(@Query("user_id") long id,@Query("cursor") long next,@Query("count") int count);

}
