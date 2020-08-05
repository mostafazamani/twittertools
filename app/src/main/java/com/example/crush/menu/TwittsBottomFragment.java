package com.example.crush.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crush.R;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.TweetTimelineRecyclerViewAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

public class TwittsBottomFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.twitts_fragment,container,false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        final UserTimeline userTimeline = new UserTimeline.Builder()
                .screenName("Mzamani1998")
                .build();

        final TweetTimelineRecyclerViewAdapter adapter =
                new TweetTimelineRecyclerViewAdapter.Builder(view.getContext())
                        .setTimeline(userTimeline)
                        .setViewStyle(R.style.tw__TweetLightWithActionsStyle)
                        .build();
        recyclerView.setAdapter(adapter);


        return view;
    }
}
