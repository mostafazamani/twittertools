package com.op.crush.menu;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.gson.JsonObject;
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
import retrofit2.Callback;
import retrofit2.Response;

public class FollowingNotFollowYou extends Fragment {

    ImageButton back_to_homefrag;
    Button unfollow_all;
    DbFollow db;
    List<follow> followList;
    FolloweingNfyAdapter ynfAdapter;
    private ListView list;
    private TextView txtProgress;
    private ProgressBar progressBar;
    private ProgressViewModel model;
    private int stat;
    private TwitterSession session;
    private RewardedVideoAd mRewardedVideoAd;
    SwipeRefreshLayout refreshLayout;
    SharedPreferences preferences;
    private SharedPreferences preferences1;
    boolean adl = false;
    ProgressDialog dialogf;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.following_nfy_fragment, container, false);

        // ((MainMenu)getActivity()).getSupportActionBar().hide();//Toolbar hidden
        unfollow_all = view.findViewById(R.id.unfollow_all);
        list = view.findViewById(R.id.list_fnfy);
        txtProgress = view.findViewById(R.id.txtProgress1);
        progressBar = view.findViewById(R.id.progressBar1);

        refreshLayout = view.findViewById(R.id.swipu);

        session = TwitterCore.getInstance().getSessionManager().getActiveSession();

        ProgressDialog dialog = new ProgressDialog(view.getContext());
        dialog.setMessage("wait");
        dialog.setCancelable(false);

        dialogf = new ProgressDialog(view.getContext());
        dialogf.setMessage("UnFollowing...");
        dialogf.setCancelable(false);

        preferences = view.getContext().getSharedPreferences("Courser", Context.MODE_PRIVATE);
        preferences1 = view.getContext().getSharedPreferences("AdL", Context.MODE_PRIVATE);
        String a = preferences1.getString("ad", "true");
        if (a.equals("true"))
            adl = true;

        if (preferences.getInt("FollowerCount", 0) == 1 && preferences.getInt("FollowingCount", 0) == 1) {
            progressBar.setVisibility(View.INVISIBLE);
            ynfAdapter = new FolloweingNfyAdapter(view.getContext());
            list.setAdapter(ynfAdapter);

            db = DbFollow.getInstance(view.getContext());
            db.getReadableDatabase();


            followList = db.getExpectItem(DbFollow.TB_FOLLOWING, DbFollow.TB_FOLLOWER);
            ynfAdapter.AddToList(followList);
            ynfAdapter.notifyDataSetChanged();
            db.close();


        }

        MobileAds.initialize(view.getContext(), "ca-app-pub-6353098097853332~3028901753");
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(view.getContext());


        final int[] x = {0};
        model = new ViewModelProvider(getActivity()).get(ProgressViewModel.class);
        model.getState().observe(getViewLifecycleOwner(), new Observer<List<ProgressState>>() {
            @Override
            public void onChanged(List<ProgressState> progressStates) {
                stat = progressStates.get(progressStates.size() - 1).getState();
                progressBar.setProgress(stat);
                txtProgress.setText(String.valueOf(stat) + "%");
                if (stat == 100) {
                    x[0] += 1;

                    if (preferences.getInt("FollowerCount", 0) == 1 && preferences.getInt("FollowingCount", 0) == 1) {
                        progressBar.setVisibility(View.INVISIBLE);
                        txtProgress.setVisibility(View.INVISIBLE);
                        ynfAdapter = new FolloweingNfyAdapter(view.getContext());
                        list.setAdapter(ynfAdapter);

                        db = DbFollow.getInstance(view.getContext());
                        db.getReadableDatabase();


                        followList = db.getExpectItem(DbFollow.TB_FOLLOWING, DbFollow.TB_FOLLOWER);
                        ynfAdapter.AddToList(followList);
                        ynfAdapter.notifyDataSetChanged();
                        db.close();


                    }
                }
            }
        });


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (preferences.getInt("FollowerCount", 0) == 1 && preferences.getInt("FollowingCount", 0) == 1) {
                    ynfAdapter = new FolloweingNfyAdapter(view.getContext());
                    list.setAdapter(ynfAdapter);

                    db = DbFollow.getInstance(view.getContext());
                    db.getReadableDatabase();


                    followList = db.getExpectItem(DbFollow.TB_FOLLOWING, DbFollow.TB_FOLLOWER);
                    ynfAdapter.AddToList(followList);
                    ynfAdapter.notifyDataSetChanged();
                    db.close();

                    refreshLayout.setRefreshing(false);
                }

            }
        });


        //    model = new ViewModelProvider(getActivity()).get(ProgressViewModel.class);
//        model.getState().observe(getViewLifecycleOwner(), new Observer<List<ProgressState>>() {
//            @Override
//            public void onChanged(List<ProgressState> progressStates) {
//                stat = progressStates.get(progressStates.size() - 1).getState();
//                progressBar.setProgress(stat);
//                txtProgress.setText(String.valueOf(stat) + "%");
//                if (stat == 100) {
//                    progressBar.setVisibility(View.INVISIBLE);
//                    ynfAdapter = new FolloweingNfyAdapter(view.getContext());
//                    list.setAdapter(ynfAdapter);
//
//                    db = DbFollow.getInstance(view.getContext());
//                    db.getReadableDatabase();
//
//                    fo = new ArrayList<>();
//
//                    followList = db.getExpectItem(DbFollow.TB_FOLLOWING,DbFollow.TB_FOLLOWER);
//                    ynfAdapter.AddToList(followList);
//                    ynfAdapter.notifyDataSetChanged();
////                    for (int i = 0; i < followList.size(); i++) {
////
////                        if (!db.CheckItem(followList.get(i).getId(), DbFollow.TB_FOLLOWER)) {
////                            fo.add(db.getOneItem(followList.get(i).getId(), DbFollow.TB_FOLLOWING));
////
////                        }
////
////                        if (i == followList.size() - 1) {
////                            ynfAdapter.AddToList(fo);
////                            ynfAdapter.notifyDataSetChanged();
////                        }
////
////
////                    }
//                }
//            }
//        });


        unfollow_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (adl) {
                    dialog.show();
                    mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
                        @Override
                        public void onRewardedVideoAdLoaded() {
                            Log.i("ad", "loaded");
                            dialog.dismiss();
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
                            dialogf.show();
                            int r = followList.size();

                            if (r > 15) {
                                for (int q = 0; q <= 15; q++) {
                                    new AllUnFollow(session, ynfAdapter, getContext(), followList.get(q).getId()).execute();
                                }
                                dialogf.dismiss();
                            } else if (r != 0) {
                                for (int q = 0; q < r - 1; q++) {
                                    new AllUnFollow(session, ynfAdapter, getContext(), followList.get(q).getId()).execute();
                                }
                                dialogf.dismiss();
                            }

                        }

                        @Override
                        public void onRewardedVideoAdLeftApplication() {
                            Log.i("ad", "left");
                        }

                        @Override
                        public void onRewardedVideoAdFailedToLoad(int i) {
                            Log.i("ad", "filed");
                            dialog.dismiss();
                            dialogf.show();
                            int r = followList.size();

                            if (r > 15) {
                                for (int q = 0; q <= 15; q++) {
                                    new AllUnFollow(session, ynfAdapter, getContext(), followList.get(q).getId()).execute();
                                }
                                dialogf.dismiss();
                            } else if (r != 0) {
                                for (int q = 0; q < r - 1; q++) {
                                    new AllUnFollow(session, ynfAdapter, getContext(), followList.get(q).getId()).execute();

                                }
                                dialogf.dismiss();
                            }

                        }

                        @Override
                        public void onRewardedVideoCompleted() {
                            Log.i("ad", "completed");
                        }
                    });

                    loadRewardedVideoAd();
                } else {
                    dialogf.show();
                    int r = followList.size();

                    if (r > 15) {
                        for (int q = 0; q <= 15; q++) {
                            new AllUnFollow(session, ynfAdapter, getContext(), followList.get(q).getId()).execute();
                            if (q == 15)
                                dialogf.dismiss();
                        }
                    } else if (r != 0) {
                        for (int q = 0; q < r - 1; q++) {
                            new AllUnFollow(session, ynfAdapter, getContext(), followList.get(q).getId()).execute();
                            if (q == r - 2)
                                dialogf.dismiss();
                        }
                    }
                }
                followList = db.getExpectItem(DbFollow.TB_FOLLOWING, DbFollow.TB_FOLLOWER);
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
        Context context;
        public int j;
        long id;

        public AllUnFollow(TwitterSession session1, FolloweingNfyAdapter ynfAdapter, Context db, long id) {
            this.session1 = session1;
            this.ynfAdapter = ynfAdapter;
            this.context = db;
            this.id = id;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            db = DbFollow.getInstance(context);
            db.getReadableDatabase();
            db.getWritableDatabase();

            MyTwitterApiClient apiClient = new MyTwitterApiClient(session1);
            apiClient.getCustomTwitterService().DestroyFollow(id).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.body() != null) {
                        Log.i("ad", "Destroy follow");
                        ynfAdapter.RemoveList(0);
                        ynfAdapter.notifyDataSetChanged();
                        db.DeleteItem(id, DbFollow.TB_FOLLOWING);
                        db.close();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        //        public void UnFollow() {
//            MyTwitterApiClient apiClient = new MyTwitterApiClient(session1);
//            apiClient.getCustomTwitterService().DestroyFollow(fo.get(j).getId()).enqueue(new Callback<JsonObject>() {
//                @Override
//                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                    if (response.body() != null) {
//                        Log.i("ad", "Destroy follow");
//                        ynfAdapter.RemoveList(0);
//                        ynfAdapter.notifyDataSetChanged();
//                        db.DeleteItem(fo.get(j).getId(), DbFollow.TB_FOLLOWING);
//                        db.close();
//                        j += 1;
//
//
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<JsonObject> call, Throwable t) {
//
//                }
//            });
//        }
    }
}

