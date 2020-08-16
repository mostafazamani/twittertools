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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.crush.DbSuggest;
import com.example.crush.R;
import com.example.crush.models.SuggestUser;
import com.example.crush.models.follow;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class FollowerYnfAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<follow> ex;

    private TwitterSession session;

    // 1
    public FollowerYnfAdapter(Context context) {
        ex = new ArrayList<>();
        this.mContext = context;


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

