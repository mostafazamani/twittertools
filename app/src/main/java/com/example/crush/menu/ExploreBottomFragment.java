package com.example.crush.menu;

//import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.crush.DbSuggest;
import com.example.crush.R;
import com.example.crush.adapter.ExploreAdapter;
import com.example.crush.adapter.homeTimeline;
import com.example.crush.models.SuggestUser;
import com.example.crush.models.follow;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.ArrayList;
import java.util.List;

public class ExploreBottomFragment extends Fragment {
    homeTimeline adapter;
    private TwitterSession session;

    int j = 0;
    List<SuggestUser> sl;


    DbSuggest dbSuggest;


    GridView gridView;
    private ExploreAdapter exploreAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Twitter.initialize(getContext());
        final View view = inflater.inflate(R.layout.explore_fragment, container, false);
        session = TwitterCore.getInstance().getSessionManager().getActiveSession();


        dbSuggest = new DbSuggest(view.getContext());
        dbSuggest.getReadableDatabase();


        sl = dbSuggest.getItem();


        ////////////////GridView//////////////////////////
        gridView = view.findViewById(R.id.gridview);
        exploreAdapter = new ExploreAdapter(view.getContext(),sl);
        gridView.setAdapter(exploreAdapter);



        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if (absListView.getLastVisiblePosition() == i - 1) {
//                    j += 30;
//                    if (j+30 <= sl.size()){
//
//                        exploreAdapter.AddItemToList(getSl(sl, j));
//                        exploreAdapter.notifyDataSetChanged();
//                    }




                }

            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }


    public boolean cheklist(List<follow> list, long id) {
        boolean b = false;

        for (int i = 0; i < list.size(); i++) {

            if (list.get(i).getId() == id) {
                b = false;
            } else {
                b = true;
            }

        }

        return b;
    }

    public List<SuggestUser> getSl(List<SuggestUser> sl, int x) {
        List<SuggestUser> list = new ArrayList<>();
        for (int i = x; i < x + 30; i++)
            list.add(sl.get(i));

        return list;
    }


}
