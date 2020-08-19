package com.op.crush.menu;

//import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.op.crush.DbSuggest;
import com.op.crush.R;
import com.op.crush.adapter.ExploreAdapter;
import com.op.crush.adapter.homeTimeline;
import com.op.crush.models.SuggestUser;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.List;

public class ExploreBottomFragment extends Fragment {
    homeTimeline adapter;
    private TwitterSession session;

    int j = 0;
    List<SuggestUser> sl;

    Button add_to_sugest;

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

        add_to_sugest = view.findViewById(R.id.add_to_sugest);
        add_to_sugest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

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





}
