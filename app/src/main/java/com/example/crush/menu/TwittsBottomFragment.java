package com.example.crush.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crush.MyTwitterApiClient;
import com.example.crush.R;
import com.example.crush.adapter.homeTimeline;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetui.Timeline;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.TweetTimelineRecyclerViewAdapter;
import com.twitter.sdk.android.tweetui.TwitterListTimeline;
import com.twitter.sdk.android.tweetui.UserTimeline;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class TwittsBottomFragment extends Fragment {

    homeTimeline adapter;
    private TwitterSession session;
    List<Long> list;
    RecyclerView recyclerView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Twitter.initialize(getContext());
        View view = inflater.inflate(R.layout.twitts_fragment,container,false);



        session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        recyclerView = view.findViewById(R.id.timeline_item);
        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(llm);
        adapter = new homeTimeline(view.getContext());
        list = new ArrayList<>();
        recyclerView.setAdapter(adapter);


        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(session);
        myTwitterApiClient.getCustomTwitterService().HomeTimeline(100).enqueue(new retrofit2.Callback() {
            @Override
            public void onResponse(Call call, @NonNull Response response) {
                if (response.body() != null) {
                    try {
                        JsonArray elements = (JsonArray) response.body();

                        for (int i = 0; i < 99; i++) {

                            JsonObject jsonObject = (JsonObject) elements.get(i);
                            JsonElement f = jsonObject.get("id");


                            list.add(jsonObject.get("id").getAsLong());


                        }

                    } catch (Exception e) {
                        Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    adapter.AddItemToList(list);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {

                Toast.makeText(getContext(), "wtf" + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });



//        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
//        session = TwitterCore.getInstance().getSessionManager().getActiveSession();
//        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
//        final UserTimeline userTimeline = new UserTimeline.Builder()
//                .screenName(session.getUserName())
//                .build();
//
//        final TweetTimelineRecyclerViewAdapter adapter =
//                new TweetTimelineRecyclerViewAdapter.Builder(view.getContext())
//                        .setTimeline(userTimeline)
//                        .setViewStyle(R.style.tw__TweetLightWithActionsStyle)
//                        .build();
//        recyclerView.setAdapter(adapter);


        return view;
    }
}
