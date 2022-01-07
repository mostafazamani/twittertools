package com.opteam.tools.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.opteam.tools.MyTwitterApiClient;
import com.opteam.tools.R;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrushsAdapter extends BaseAdapter {

    private TwitterSession session;
    private List<Long> l = new ArrayList<>();
    private Context context;

    public CrushsAdapter( Context context) {
        this.context = context;
    }
    public void AddToList(List<Long> list){
        l.addAll(list);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return l.size();
    }

    @Override
    public Object getItem(int i) {
        return l.get(i);
    }

    @Override
    public long getItemId(int i) {
        return l.get(i);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.crushs_list_item, null);
        }
        TextView name = convertView.findViewById(R.id.profile_name_crushs_list);
        TextView screen_name = convertView.findViewById(R.id.profile_id_crushs_list);
        ImageView img = convertView.findViewById(R.id.profile_image_crushs_list);
        Button btn = convertView.findViewById(R.id.massage_btn_crushs_list);

        Log.i("crushslist","adapter");
        session = TwitterCore.getInstance().getSessionManager().getActiveSession();

        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(session);
        myTwitterApiClient.getCustomTwitterService().SeeUserInfo(l.get(i)).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.body() != null) {


                    try {
                        JsonArray elements = (JsonArray) response.body();


                        JsonObject jsonObject = (JsonObject) elements.get(0);
                        name.setText(jsonObject.get("name").getAsString());
                        screen_name.setText(jsonObject.get("screen_name").getAsString());
                        Picasso.with(context).load(jsonObject.get("profile_image_url").getAsString()).into(img);


                    } catch (Exception e) {
                        Toast.makeText(context, "seeUserInfo :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

            }
        });


        return convertView;
    }


}
