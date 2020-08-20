package com.op.crush.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.op.crush.DbFollowings;
import com.op.crush.MyTwitterApiClient;
import com.op.crush.R;
import com.op.crush.models.follow;
import com.op.crush.models.followmodel;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


public class FollowerYnfAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<follow> ex;
    DbFollowings dbFollowings;
    private TwitterSession session;

    // 1
    public FollowerYnfAdapter(Context context) {
        ex = new ArrayList<>();
        this.mContext = context;
        dbFollowings = new DbFollowings(context);


    }

    public void AddToList(List<follow> list){
        ex.addAll(list);

    }


    // 2
    @Override
    public int getCount() {
        return ex.size();
    }

    // 3
    @Override
    public long getItemId(int position) {
        return ex.get(position).getId();
    }

    // 4
    @Override
    public Object getItem(int position) {
        return ex.get(position);
    }

    // 5
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // 1
        //   final Book book = books[position];

        // 2
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.follower_ynf_item, null);
        }
        session = TwitterCore.getInstance().getSessionManager().getActiveSession();

        final ImageView profilePic = (ImageView) convertView.findViewById(R.id.profile_image_fynf);
        final TextView textname = (TextView) convertView.findViewById(R.id.profile_name_fynf);
        final TextView idname = (TextView) convertView.findViewById(R.id.profile_id_fynf);
        final Button follow_btn = (Button) convertView.findViewById(R.id.follow_btn_fynf);
        //final ImageView imageViewFavorite = (ImageView)convertView.findViewById(R.id.imageview_favorite);

        String purl = ex.get(position).getProfilePictureUrl();
        String url = geturlpic(purl);
        textname.setText(ex.get(position).getName());
        idname.setText(ex.get(position).getScreenName());

        Picasso.with(convertView.getContext()).load(url).into(profilePic);

        follow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                MyTwitterApiClient apiClient = new MyTwitterApiClient(session);
                apiClient.getCustomTwitterService().CreateFollow(ex.get(position).getId()).enqueue(new retrofit2.Callback() {
                    @Override
                    public void onResponse(Call call, @NonNull Response response) {
                        if (response.body() != null) {
                            ex.remove(position);
                            dbFollowings.getWritableDatabase();
                            dbFollowings.AddItem(ex.get(position));

                            notifyDataSetChanged();

                        }
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {


                    }
                });


            }
        });



        return convertView;
    }

    public String geturlpic(String s) {
        char[] chars = s.toCharArray();
        String url = "";
        for (int i = 0; i < chars.length - 11; i++) {
            url += chars[i];
        }

        url += ".jpg";

        return url;
    }


}

