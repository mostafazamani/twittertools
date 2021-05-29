package com.op.crush.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.op.crush.DbFollow;
import com.op.crush.DbSuggest;
import com.op.crush.MyTwitterApiClient;
import com.op.crush.R;


import com.op.crush.models.SuggestUser;


import com.op.crush.models.follow;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


public class ExploreAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<SuggestUser> ex = new ArrayList<>();
    private final List<SuggestUser> users;
    private TwitterSession session;

    // 1
    public ExploreAdapter(Context context ) {
        this.mContext = context;
        DbSuggest suggest = new DbSuggest(context);
        suggest.getReadableDatabase();
        users = suggest.getItem();

    }

    public void AddToList(SuggestUser list){
        ex.add(list);

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
            convertView = layoutInflater.inflate(R.layout.explore_item, null);
        }
        session = TwitterCore.getInstance().getSessionManager().getActiveSession();

        final ImageView profilePic = (ImageView) convertView.findViewById(R.id.profile_image);
        final TextView textname = (TextView) convertView.findViewById(R.id.profile_name);
        final TextView idname = (TextView) convertView.findViewById(R.id.profile_id);
        ImageView button = convertView.findViewById(R.id.add_to_follower);
        //final ImageView imageViewFavorite = (ImageView)convertView.findViewById(R.id.imageview_favorite);

        ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("Following...");
        dialog.setCancelable(false);

        String purl = ex.get(position).getProfilePictureUrl();
        String url = geturlpic(purl);
        textname.setText(ex.get(position).getName());
        idname.setText(ex.get(position).getScreenName());

        Picasso.with(convertView.getContext()).load(url).into(profilePic);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                MyTwitterApiClient apiClient = new MyTwitterApiClient(session);
                apiClient.getCustomTwitterService().CreateFollow(ex.get(position).getId()).enqueue(new retrofit2.Callback() {
                    @Override
                    public void onResponse(Call call, @NonNull Response response) {
                        if (response.body() != null) {

                            DbFollow dbFollowings = DbFollow.getInstance(mContext);
                            dbFollowings.getWritableDatabase();
                            SuggestUser s = ex.get(position);
                            follow f = new follow();
                            f.setId(s.getId());
                            f.setName(s.getName());
                            f.setScreenName(s.getScreenName());
                            f.setProfilePictureUrl(s.getProfilePictureUrl());
                            dbFollowings.AddItem(f, DbFollow.TB_FOLLOWING);
                            dbFollowings.close();
                            ex.remove(position);
                            notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        Toast.makeText(mContext, "try again", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

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
