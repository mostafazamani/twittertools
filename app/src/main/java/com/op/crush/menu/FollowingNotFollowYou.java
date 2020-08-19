package com.op.crush.menu;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.op.crush.DbFollowers;
import com.op.crush.DbFollowings;
import com.op.crush.R;
import com.op.crush.adapter.FollowerYnfAdapter;
import com.op.crush.models.follow;

import java.util.ArrayList;
import java.util.List;

public class FollowingNotFollowYou  extends Fragment {

    ImageButton back_to_homefrag;
    Button unfollow_all;
    DbFollowers dbFollowers;
    DbFollowings dbFollowings;
    List<follow> followList;
    List<follow> fo;
    FollowerYnfAdapter ynfAdapter;
    private ListView list;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.following_nfy_fragment, container, false);

        // ((MainMenu)getActivity()).getSupportActionBar().hide();//Toolbar hidden

        back_to_homefrag = view.findViewById(R.id.fynf_back);
        unfollow_all = view.findViewById(R.id.follow_all);
        list = view.findViewById(R.id.list_fynf);


        back_to_homefrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment home_fragment = new HomeBottomFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, home_fragment ); // give your fragment container id in first parameter
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();
            }
        });

        ynfAdapter = new FollowerYnfAdapter(view.getContext());
        list.setAdapter(ynfAdapter);

        dbFollowers = new DbFollowers(view.getContext());
        dbFollowings = new DbFollowings(view.getContext());
        dbFollowings.getReadableDatabase();
        dbFollowers.getReadableDatabase();
        fo = new ArrayList<>();

        followList = dbFollowers.getItem();

        for (int i = 0 ; i < followList.size() ; i++){

            if (!dbFollowings.CheckItem(followList.get(i).getId())){
                fo.add(dbFollowers.getOneItem(followList.get(i).getId()));

            }

            if (i == followList.size() -1) {
                ynfAdapter.AddToList(fo);
                ynfAdapter.notifyDataSetChanged();
            }


        }
        return view;
    }
}
