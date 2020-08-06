package com.example.crush.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crush.R;
import com.example.crush.models.following;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;

import java.util.ArrayList;
import java.util.List;

public class homeTimeline extends RecyclerView.Adapter<homeTimeline.ViewHolder> {

    private List<Long> list = new ArrayList<>();
    private Context context;
    private LayoutInflater mInflater;

    public homeTimeline(Context context) {
        this.mInflater = LayoutInflater.from(context);

        this.context = context;
    }

    public void AddItemToList(List<Long> l) {
        this.list = l;
        this.notifyDataSetChanged();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.home_timeline, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


        TweetUtils.loadTweet(list.get(position), new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {

                holder.myLayout.addView(new TweetView(context, result.data, R.style.tw__TweetLightWithActionsStyle));

            }

            @Override
            public void failure(TwitterException exception) {
                // Toast.makeText(...).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout myLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            myLayout = itemView.findViewById(R.id.my_tweet_layout);
        }

    }
}
