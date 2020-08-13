package com.example.crush.menu;

//import android.support.v4.app.Fragment;

import android.content.Context;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.crush.DbFollowers;
import com.example.crush.DbSuggest;
import com.example.crush.MyTwitterApiClient;
import com.example.crush.R;
import com.example.crush.adapter.ExploreAdapter;
import com.example.crush.adapter.homeTimeline;
import com.example.crush.models.SuggestUser;
import com.example.crush.models.following;
import com.example.crush.models.followingmodel;
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
    List<SuggestUser> sl;

    DbFollowers db;
    DbSuggest dbSuggest;
    public long nextCursor = -1L;


    GridView gridView;
    private ExploreAdapter exploreAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Twitter.initialize(getContext());
        final View view = inflater.inflate(R.layout.explore_fragment, container, false);
        session = TwitterCore.getInstance().getSessionManager().getActiveSession();

        db = new DbFollowers(view.getContext());
        db.getReadableDatabase();

        dbSuggest = new DbSuggest(view.getContext());
        dbSuggest.getWritableDatabase();

        list = db.getItem();
        sl = new ArrayList<>();




        ////////////////GridView//////////////////////////
        gridView = view.findViewById(R.id.gridview);
        exploreAdapter = new ExploreAdapter(view.getContext() );
        gridView.setAdapter(exploreAdapter);
        loginMethod(session,nextCursor,1242062707190366211L,view.getContext());

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                    if (absListView.getLastVisiblePosition() == i-1){
                        j+=1;
                      //  loginMethod(session,nextCursor,list.get(j));

                    }

            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




    }

    public void loginMethod(final TwitterSession twitterSession, long next , final long id, final Context context) {


        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(twitterSession);
        myTwitterApiClient.getCustomTwitterService().FollowersList(id, next, 200).enqueue(new retrofit2.Callback() {
            @Override
            public void onResponse(Call call, @NonNull Response response) {
                if (response.body() != null) {
                    followingmodel fol = (followingmodel) response.body();
                    SuggestUser fl = new SuggestUser();
                    if (fol.getResults() != null)
                        for (int i = 0 ; i < fol.getResults().size() ; i++){

                            if (!db.CheckItem(fol.getResults().get(i).getId()) && session.getId() != fol.getResults().get(i).getId()){
                                fl.setId(fol.getResults().get(i).getId());
                                fl.setName(fol.getResults().get(i).getName());
                                fl.setScreenName(fol.getResults().get(i).getScreenName());
                                fl.setProfilePictureUrl(fol.getResults().get(i).getProfilePictureUrl());
                                dbSuggest.AddItem(fl);
                                sl.add(fl);


                            }
                            exploreAdapter.AddItemToList(sl);
                            exploreAdapter.notifyDataSetChanged();


                        }

                    Toast.makeText(getContext(), ""+sl, Toast.LENGTH_SHORT).show();


                    if (fol.getNextCursor() != 0) loginMethod(twitterSession, fol.getNextCursor(),id,context);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getContext(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });



    }


    public boolean cheklist(List<following> list , long id){
        boolean b = false;
        
        for (int i = 0 ; i< list.size() ;i++){
            
            if (list.get(i).getId() == id){
                b = false;
            }else {
                b = true;
            }
            
        }
        
        return b;
    }
    
}
