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
import com.op.crush.adapter.FolloweingNfyAdapter;
import com.op.crush.models.follow;

import java.util.ArrayList;
import java.util.List;

public class FollowingNotFollowYou  extends Fragment {

    ImageButton back_to_homefrag;
    Button unfollow_all;
    DbFollow db;
    List<follow> followList;
    List<follow> fo;
    FolloweingNfyAdapter ynfAdapter;
    private ListView list;
    private TextView txtProgress;
    private ProgressBar progressBar;
    private ProgressViewModel model;
    private int stat;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.following_nfy_fragment, container, false);

        // ((MainMenu)getActivity()).getSupportActionBar().hide();//Toolbar hidden

        back_to_homefrag = view.findViewById(R.id.fnfy_back);
        unfollow_all = view.findViewById(R.id.unfollow_all);
        list = view.findViewById(R.id.list_fnfy);
        txtProgress = view.findViewById(R.id.txtProgress1);
        progressBar = view.findViewById(R.id.progressBar1);


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


        model = new ViewModelProvider(getActivity()).get(ProgressViewModel.class);
        model.getState().observe(getViewLifecycleOwner(), new Observer<List<ProgressState>>() {
            @Override
            public void onChanged(List<ProgressState> progressStates) {
                stat = progressStates.get(progressStates.size() - 1).getState();
                progressBar.setProgress(stat);
                txtProgress.setText(String.valueOf(stat)+"%");
                if (stat == 100){
                    progressBar.setVisibility(View.INVISIBLE);
                    ynfAdapter = new FolloweingNfyAdapter(view.getContext());
                    list.setAdapter(ynfAdapter);

                    db = DbFollow.getInstance(view.getContext());
                    db.getReadableDatabase();

                    fo = new ArrayList<>();

                    followList = db.getItem(DbFollow.TB_FOLLOWING);

                    for (int i = 0 ; i < followList.size() ; i++){

                        if (!db.CheckItem(followList.get(i).getId(),DbFollow.TB_FOLLOWER)){
                            fo.add(db.getOneItem(followList.get(i).getId(),DbFollow.TB_FOLLOWING));

                        }

                        if (i == followList.size() -1) {
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

