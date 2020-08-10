package com.example.crush.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.crush.MyTwitterApiClient;
import com.example.crush.R;
import com.example.crush.models.following;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ExploreAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<Long> ex;
    private TwitterSession session;

    // 1
    public ExploreAdapter(Context context, List<Long> ex) {
        this.mContext = context;
        this.ex = ex;
    }

    // 2
    @Override
    public int getCount() {
        return ex.size();
    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 4
    @Override
    public Object getItem(int position) {
        return ex.get(position);
    }

    // 5
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1
        //   final Book book = books[position];

        // 2
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.explore_item, null);
        }
        session = TwitterCore.getInstance().getSessionManager().getActiveSession();

        final ImageView profilePic = (ImageView) convertView.findViewById(R.id.imageview_cover_art);
        final TextView textname = (TextView) convertView.findViewById(R.id.textview_book_name);
        final TextView idname = (TextView) convertView.findViewById(R.id.textview_book_author);
        //final ImageView imageViewFavorite = (ImageView)convertView.findViewById(R.id.imageview_favorite);

        ///////////////////////////////////////////


        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(session);
        myTwitterApiClient.getCustomTwitterService().userlookup(ex.get(position)).enqueue(new retrofit2.Callback() {
            @Override
            public void onResponse(Call call, @NonNull Response response) {
                if (response.body() != null) {
                    try {
                        JsonArray elements = (JsonArray) response.body();


                        List<following> jsonObject = (List<following>) elements.get(0);


                   //  profilePic.setImageResource(.getImageResource());
                     textname.setText(jsonObject.get(0).getName());
                     idname.setText(jsonObject.get(0).getScreenName());

                    } catch (Exception e) {

                    }

                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {


            }
        });


        /////////////////////////////////////////
        // 3

        // 4

        return convertView;
    }

}
