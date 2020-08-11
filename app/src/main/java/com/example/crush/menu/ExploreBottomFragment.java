package com.example.crush.menu;

//import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crush.DbHelper;
import com.example.crush.HomeActivity;
import com.example.crush.MyTwitterApiClient;
import com.example.crush.R;
import com.example.crush.adapter.ExploreAdapter;
import com.example.crush.adapter.homeTimeline;
import com.example.crush.models.following;
import com.example.crush.models.followingmodel;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ExploreBottomFragment extends Fragment {
    homeTimeline adapter;
    private TwitterSession session;
    List<Long> list;
    int j = 0;
    List<following> sl;
    RecyclerView recyclerView;
    DbHelper db;
    public long nextCursor = -1L;

    GridView gridView;
    private ExploreAdapter exploreAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Twitter.initialize(getContext());
        final View view = inflater.inflate(R.layout.explore_fragment, container, false);
        session = TwitterCore.getInstance().getSessionManager().getActiveSession();

        db = new DbHelper(view.getContext());
        db.getReadableDatabase();

        list = db.getItem();
        sl = new ArrayList<>();



        ////////////////GridView//////////////////////////
        gridView = view.findViewById(R.id.gridview);
        exploreAdapter = new ExploreAdapter(view.getContext() );
        gridView.setAdapter(exploreAdapter);

        loginMethod(session,nextCursor,list.get(j));

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                    if (absListView.getLastVisiblePosition() == i-1){
                        j+=1;
                        loginMethod(session,nextCursor,list.get(j));

                    }

            }
        });

        return view;
    }


    public void loginMethod(final TwitterSession twitterSession, long next , final long id) {


        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(twitterSession);
        myTwitterApiClient.getCustomTwitterService().FollowersList(id, next, 200).enqueue(new retrofit2.Callback() {
            @Override
            public void onResponse(Call call, @NonNull Response response) {
                if (response.body() != null) {
                    followingmodel fol = (followingmodel) response.body();
                    if (fol.getResults() != null)
                        for (int i = 0 ; i < fol.getResults().size() ; i++){
                                following fl = new following();
                            if (!db.CheckItem(fol.getResults().get(i).getId())){
                                fl.setId(fol.getResults().get(i).getId());
                                fl.setName(fol.getResults().get(i).getName());
                                fl.setScreenName(fol.getResults().get(i).getScreenName());
                                fl.setProfilePictureUrl(fol.getResults().get(i).getProfilePictureUrl());


                                sl.add(fl);
                            }


                        }

                    Toast.makeText(getContext(), ""+sl, Toast.LENGTH_SHORT).show();
                    exploreAdapter.AddItemToList(sl);
                    exploreAdapter.notifyDataSetChanged();


                    if (fol.getNextCursor() != 0) loginMethod(twitterSession, fol.getNextCursor(),id);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getContext(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });



    }


}
