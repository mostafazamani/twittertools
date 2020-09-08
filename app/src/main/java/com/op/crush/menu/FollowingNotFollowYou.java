package com.op.crush.menu;


import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import androidx.room.Update;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.op.crush.DbFollow;
import com.op.crush.MyTwitterApiClient;
import com.op.crush.R;
import com.op.crush.Room.ProgressState;
import com.op.crush.Room.ProgressViewModel;
import com.op.crush.adapter.FolloweingNfyAdapter;
import com.op.crush.models.follow;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class FollowingNotFollowYou extends Fragment {

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
    private TwitterSession session;
    private RewardedVideoAd mRewardedVideoAd;

    int j = 0;


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

        session = TwitterCore.getInstance().getSessionManager().getActiveSession();


        MobileAds.initialize(view.getContext(), "ca-app-pub-6353098097853332~3028901753");
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(view.getContext());


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
                txtProgress.setText(String.valueOf(stat) + "%");
                if (stat == 100) {
                    progressBar.setVisibility(View.INVISIBLE);
                    ynfAdapter = new FolloweingNfyAdapter(view.getContext());
                    list.setAdapter(ynfAdapter);

                    db = DbFollow.getInstance(view.getContext());
                    db.getReadableDatabase();

                    fo = new ArrayList<>();

                    followList = db.getItem(DbFollow.TB_FOLLOWING);

                    for (int i = 0; i < followList.size(); i++) {

                        if (!db.CheckItem(followList.get(i).getId(), DbFollow.TB_FOLLOWER)) {
                            fo.add(db.getOneItem(followList.get(i).getId(), DbFollow.TB_FOLLOWING));

                        }

                        if (i == followList.size() - 1) {
                            ynfAdapter.AddToList(fo);
                            ynfAdapter.notifyDataSetChanged();
                        }


                    }
                }
            }
        });


        unfollow_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                j = 0;
                mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
                    @Override
                    public void onRewardedVideoAdLoaded() {
                        Log.i("ad", "loaded");
                        mRewardedVideoAd.show();
                    }

                    @Override
                    public void onRewardedVideoAdOpened() {
                        Log.i("ad", "open");
                    }

                    @Override
                    public void onRewardedVideoStarted() {
                        Log.i("ad", "start");
                    }

                    @Override
                    public void onRewardedVideoAdClosed() {
                        Log.i("ad", "vclose");

                    }

                    @Override
                    public void onRewarded(RewardItem rewardItem) {
                        Log.i("ad", "reward");
                        new AllUnFollow(session, ynfAdapter, db, fo).execute();

                    }

                    @Override
                    public void onRewardedVideoAdLeftApplication() {
                        Log.i("ad", "left");
                    }

                    @Override
                    public void onRewardedVideoAdFailedToLoad(int i) {
                        Log.i("ad", "filed");

                    }

                    @Override
                    public void onRewardedVideoCompleted() {
                        Log.i("ad", "completed");
                    }
                });

                loadRewardedVideoAd();
            }
        });


        return view;
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-6353098097853332/2923307589",
                new AdRequest.Builder().build());
    }

    public static class AllUnFollow extends AsyncTask<Void, Void, Void> {

        TwitterSession session1;
        FolloweingNfyAdapter ynfAdapter;
        DbFollow db;
        public int j;
        List<follow> fo;

        public AllUnFollow(TwitterSession session1, FolloweingNfyAdapter ynfAdapter, DbFollow db, List<follow> fo) {
            this.session1 = session1;
            this.ynfAdapter = ynfAdapter;
            this.db = db;
            this.fo = fo;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            j = 0;
            UnFollow();
            return null;
        }

        public void UnFollow() {
            MyTwitterApiClient apiClient = new MyTwitterApiClient(session1);
            apiClient.getCustomTwitterService().DestroyFollow(fo.get(j).getId()).enqueue(new retrofit2.Callback() {
                @Override
                public void onResponse(Call call, @NonNull Response response) {
                    if (response.body() != null) {
                        Log.i("ad", "Destroy follow");
                        ynfAdapter.RemoveList(0);
                        ynfAdapter.notifyDataSetChanged();
                        db.DeleteItem(fo.get(j).getId(), DbFollow.TB_FOLLOWING);
                        db.close();
                        j += 1;

                        if (j <= 5)
                            UnFollow();

                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {


                }
            });
        }
    }
}

