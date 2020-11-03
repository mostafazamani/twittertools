package com.op.crush.menu;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.op.crush.MyTwitterApiClient;
import com.op.crush.R;
import com.op.crush.models.UserCrushSearch;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrushSearch extends Fragment   {

    private TwitterSession session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.crush_search_dialog, container, false);
        TextView textView = view.findViewById(R.id.crush_search_edittext);
        ImageButton button = view.findViewById(R.id.crush_search_button);
        session = TwitterCore.getInstance().getSessionManager().getActiveSession();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Search(session,textView.getText().toString());

            }
        });





        return view;
    }


    public void Search(TwitterSession session,String query){


        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(session);
        myTwitterApiClient.getCustomTwitterService().SearchUser(query).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                if (response.body() != null) {

                    try {
                        JsonArray elements = (JsonArray) response.body();

                        for (int i = 0 ; i< elements.size();i++) {
                            JsonObject jsonObject = (JsonObject) elements.get(i);
                            UserCrushSearch userCrushSearch =
                                    new UserCrushSearch(jsonObject.get("id").getAsLong(),
                                            jsonObject.get("name").getAsString(),jsonObject.get("screen_name").getAsString(),
                                            jsonObject.get("profile_image_url").getAsString());
                            Log.i("searchuser",userCrushSearch.getName()+"__"+userCrushSearch.getScreen_name());
                        }
                    } catch (Exception e) {
                        Log.i("searchuser","field");

                    }


                }


            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

            }
        });

    }

}
