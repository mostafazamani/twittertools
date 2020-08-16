package com.example.crush.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;


import com.example.crush.DbSuggest;
import com.example.crush.R;


import com.example.crush.models.SuggestUser;


import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.io.InputStream;
import java.util.List;


public class ExploreAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<SuggestUser> ex;
    private final List<SuggestUser> users;
    private TwitterSession session;

    // 1
    public ExploreAdapter(Context context , List<SuggestUser> list) {
        this.ex = list;
        this.mContext = context;
        DbSuggest suggest = new DbSuggest(context);
        suggest.getReadableDatabase();
        users = suggest.getItem();

    }



    // 2
    @Override
    public int getCount() {
        return ex.size();
    }

    // 3
    @Override
    public long getItemId(int position) {
        return users.get(position).getId();
    }

    // 4
    @Override
    public Object getItem(int position) {
        return users.get(position);
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
        //final ImageView imageViewFavorite = (ImageView)convertView.findViewById(R.id.imageview_favorite);

        String purl = ex.get(position).getProfilePictureUrl();
        String url = geturlpic(purl);
        textname.setText(ex.get(position).getName());
        idname.setText(ex.get(position).getScreenName());

        Picasso.with(convertView.getContext()).load(url).into(profilePic);


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
