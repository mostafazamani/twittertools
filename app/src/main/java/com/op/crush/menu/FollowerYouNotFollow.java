package com.op.crush.menu;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.op.crush.DbFollow;
import com.op.crush.R;
import com.op.crush.Room.ProgressState;
import com.op.crush.Room.ProgressViewModel;
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
    private TextView txtProgress;
    private ProgressBar progressBar;
    FollowerYnfAdapter ynfAdapter;
    ProgressViewModel model;
    int stat;
    private ListView list;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.follower_ynf_fragment, container, false);

        // ((MainMenu)getActivity()).getSupportActionBar().hide();//Toolbar hidden


        back_to_homefrag = view.findViewById(R.id.fynf_back);
        follow_all = view.findViewById(R.id.follow_all);
        list = view.findViewById(R.id.list_fynf);
        txtProgress = view.findViewById(R.id.txtProgress);
        progressBar = view.findViewById(R.id.progressBar);



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


        model = new ViewModelProvider(getActivity()).get(ProgressViewModel.class);
        model.getState().observe(getViewLifecycleOwner(), new Observer<List<ProgressState>>() {
            @Override
            public void onChanged(List<ProgressState> progressStates) {
                stat = progressStates.get(progressStates.size() - 1).getState();
                progressBar.setProgress(stat);
                txtProgress.setText(String.valueOf(stat)+"%");
                if (stat == 100){
                    progressBar.setVisibility(View.INVISIBLE);
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

                }

            }
        });








        return view;
    }
}
