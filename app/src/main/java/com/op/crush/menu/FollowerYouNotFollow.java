package com.op.crush.menu;

import android.database.sqlite.SQLiteDatabase;
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

import com.op.crush.DbFollow;
import com.op.crush.R;
import com.op.crush.adapter.FollowerYnfAdapter;
import com.op.crush.models.follow;

import java.util.ArrayList;
import java.util.List;

public class FollowerYouNotFollow extends Fragment {

    ImageButton back_to_homefrag;
    Button follow_all;
    DbFollow db;
    List<follow> followList;
    List<follow> fo;
    FollowerYnfAdapter ynfAdapter;
    private ListView list;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.follower_ynf_fragment, container, false);

        // ((MainMenu)getActivity()).getSupportActionBar().hide();//Toolbar hidden


        back_to_homefrag = view.findViewById(R.id.fynf_back);
        follow_all = view.findViewById(R.id.follow_all);
        list = view.findViewById(R.id.list_fynf);


        back_to_homefrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment home_fragment = new HomeBottomFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, home_fragment); // give your fragment container id in first parameter
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();
            }
        });

        ynfAdapter = new FollowerYnfAdapter(view.getContext());
        list.setAdapter(ynfAdapter);

        db = DbFollow.getInstance(view.getContext());




        fo = new ArrayList<>();

        followList = db.getItem(DbFollow.TB_FOLLOWER);

        for (int i = 0; i < followList.size(); i++) {

            if (!db.CheckItem(followList.get(i).getId(),DbFollow.TB_FOLLOWING)) {
                fo.add(db.getOneItem(followList.get(i).getId(),DbFollow.TB_FOLLOWER));

            }
            if (i == followList.size() - 1) {
                ynfAdapter.AddToList(fo);
                ynfAdapter.notifyDataSetChanged();
            }
        }


        return view;
    }
}
