package com.op.crush.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;


import com.op.crush.DbSuggest;
import com.op.crush.R;


import com.op.crush.models.SuggestUser;


import com.op.crush.models.follow;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.ArrayList;
import java.util.List;


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
