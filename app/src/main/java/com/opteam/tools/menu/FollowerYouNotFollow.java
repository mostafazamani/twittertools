package com.opteam.tools.menu;


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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.opteam.tools.DbFollow;
import com.opteam.tools.MyTwitterApiClient;
import com.opteam.tools.R;
import com.opteam.tools.Room.ProgressState;
import com.opteam.tools.Room.ProgressStateFollower;
import com.opteam.tools.Room.ProgressViewModel;
import com.opteam.tools.Room.ProgressViewModelFollower;
import com.opteam.tools.adapter.FollowerYnfAdapter;
import com.opteam.tools.models.follow;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class FollowerYouNotFollow extends Fragment {

    ImageButton back_to_homefrag;
    Button follow_all;
    DbFollow db;
    List<follow> followList;
    List<follow> fo;
    private TextView txtProgress;
    private TextView txtProgress2;
    private ProgressBar progressBar;
    private ProgressBar progressBar2;
    FollowerYnfAdapter ynfAdapter;
    ProgressViewModel model;
    ProgressViewModelFollower modelFollower;
    int stat;
    int statFollower;
    private ListView list;
    private TwitterSession session;
    private RewardedVideoAd mRewardedVideoAd;
    SwipeRefreshLayout refreshLayout;
    SharedPreferences preferences;
    int j = 0;
    ProgressDialog dialogf;
    private SharedPreferences preferences1;
    boolean adl = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.follower_ynf_fragment, container, false);

        // ((MainMenu)getActivity()).getSupportActionBar().hide();//Toolbar hidden


        follow_all = view.findViewById(R.id.follow_all);
        list = view.findViewById(R.id.list_fynf);
        txtProgress = view.findViewById(R.id.txtProgress);
        txtProgress2 = view.findViewById(R.id.txtProgress2);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar2 = view.findViewById(R.id.progressBar2);
        follow_all.setVisibility(View.INVISIBLE);

        refreshLayout = view.findViewById(R.id.swipy);
        refreshLayout.setEnabled(false);

        session = TwitterCore.getInstance().getSessionManager().getActiveSession();

        MobileAds.initialize(view.getContext(), "ca-app-pub-6353098097853332~3028901753");
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(view.getContext());

        ProgressDialog dialog = new ProgressDialog(view.getContext());
        dialog.setMessage("wait");
        dialog.setCancelable(false);

        dialogf = new ProgressDialog(view.getContext());
        dialogf.setMessage("Following...");
        dialogf.setCancelable(false);

        preferences = view.getContext().getSharedPreferences("Courser", Context.MODE_PRIVATE);
        preferences1 = view.getContext().getSharedPreferences("AdL", Context.MODE_PRIVATE);
        String a = preferences1.getString("ad", "true");
        if (a.equals("true"))
            adl = true;

        if (preferences.getInt("FollowerCount", 0) == 1 && preferences.getInt("FollowingCount", 0) == 1) {
            progressBar.setVisibility(View.INVISIBLE);
            progressBar2.setVisibility(View.INVISIBLE);
            txtProgress.setVisibility(View.INVISIBLE);
            txtProgress2.setVisibility(View.INVISIBLE);
            refreshLayout.setEnabled(true);
            ynfAdapter = new FollowerYnfAdapter(view.getContext());
            list.setAdapter(ynfAdapter);

            db = DbFollow.getInstance(view.getContext());
            db.getReadableDatabase();

            fo = new ArrayList<>();

            followList = db.getExpectItem(DbFollow.TB_FOLLOWER, DbFollow.TB_FOLLOWING);
            ynfAdapter.AddToList(followList);
            ynfAdapter.notifyDataSetChanged();
            db.close();

        }


        final int[] x = {0};
        model = new ViewModelProvider(getActivity()).get(ProgressViewModel.class);
        model.getState().observe(getViewLifecycleOwner(), new Observer<List<ProgressState>>() {
            @Override
            public void onChanged(List<ProgressState> progressStates) {
                stat = progressStates.get(progressStates.size() - 1).getState();
                progressBar.setProgress(stat);
                txtProgress.setText("Following\n"+String.valueOf(stat) + "%");
                if (stat == 100) {
                    if (statFollower == 100) {
                        x[0] += 1;

                        if (preferences.getInt("FollowerCount", 0) == 1 && preferences.getInt("FollowingCount", 0) == 1) {
                            progressBar.setVisibility(View.INVISIBLE);
                            progressBar2.setVisibility(View.INVISIBLE);
                            txtProgress.setVisibility(View.INVISIBLE);
                            txtProgress2.setVisibility(View.INVISIBLE);
                            follow_all.setVisibility(View.VISIBLE);
                            ynfAdapter = new FollowerYnfAdapter(view.getContext());
                            list.setAdapter(ynfAdapter);

                            db = DbFollow.getInstance(view.getContext());
                            db.getReadableDatabase();

                            fo = new ArrayList<>();

                            followList = db.getExpectItem(DbFollow.TB_FOLLOWER, DbFollow.TB_FOLLOWING);
                            ynfAdapter.AddToList(followList);
                            ynfAdapter.notifyDataSetChanged();
                            db.close();
                            refreshLayout.setEnabled(true);
                        }
                    }
                }

            }
        });


        modelFollower = new ViewModelProvider(getActivity()).get(ProgressViewModelFollower.class);
        modelFollower.getState().observe(getViewLifecycleOwner(), new Observer<List<ProgressStateFollower>>() {
            @Override
            public void onChanged(List<ProgressStateFollower> progressStates) {
                statFollower = progressStates.get(progressStates.size() - 1).getState();
                progressBar2.setProgress(stat);
                txtProgress2.setText("Follower \n" + String.valueOf(statFollower) + "%");
                if (statFollower == 100) {
                    if (stat == 100) {
                        x[0] += 1;

                        if (preferences.getInt("FollowerCount", 0) == 1 && preferences.getInt("FollowingCount", 0) == 1) {
                            progressBar.setVisibility(View.INVISIBLE);
                            progressBar2.setVisibility(View.INVISIBLE);
                            txtProgress.setVisibility(View.INVISIBLE);
                            txtProgress2.setVisibility(View.INVISIBLE);
                            follow_all.setVisibility(View.VISIBLE);
                            ynfAdapter = new FollowerYnfAdapter(view.getContext());
                            list.setAdapter(ynfAdapter);

                            db = DbFollow.getInstance(view.getContext());
                            db.getReadableDatabase();

                            fo = new ArrayList<>();

                            followList = db.getExpectItem(DbFollow.TB_FOLLOWER, DbFollow.TB_FOLLOWING);
                            ynfAdapter.AddToList(followList);
                            ynfAdapter.notifyDataSetChanged();
                            db.close();
                            refreshLayout.setEnabled(true);
                        }
                    }
                }

            }
        });


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (preferences.getInt("FollowerCount", 0) == 1 && preferences.getInt("FollowingCount", 0) == 1) {

                    ynfAdapter = new FollowerYnfAdapter(getContext());
                    list.setAdapter(ynfAdapter);

                    db = DbFollow.getInstance(getContext());
                    db.getReadableDatabase();


                    followList = db.getExpectItem(DbFollow.TB_FOLLOWER, DbFollow.TB_FOLLOWING);
                    ynfAdapter.AddToList(followList);
                    ynfAdapter.notifyDataSetChanged();
                    db.close();
                    refreshLayout.setRefreshing(false);
                }
            }
        });


//        List<String> testDeviceIds = Arrays.asList("33BE2250B43518CCDA7DE426D04EE231");
//        RequestConfiguration configuration =
//                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
//        MobileAds.setRequestConfiguration(configuration);


//        model = new ViewModelProvider(getActivity()).get(ProgressViewModel.class);
//        model.getState().observe(getViewLifecycleOwner(), new Observer<List<ProgressState>>() {
//            @Override
//            public void onChanged(List<ProgressState> progressStates) {
//                stat = progressStates.get(progressStates.size() - 1).getState();
//                progressBar.setProgress(stat);
//                txtProgress.setText(String.valueOf(stat) + "%");
//                if (stat == 100) {
//                    progressBar.setVisibility(View.INVISIBLE);
//                    ynfAdapter = new FollowerYnfAdapter(view.getContext());
//                    list.setAdapter(ynfAdapter);
//
//                    db = DbFollow.getInstance(view.getContext());
//
//
//                    fo = new ArrayList<>();
//
//                    followList = db.getExpectItem(DbFollow.TB_FOLLOWER,DbFollow.TB_FOLLOWING);
//                    ynfAdapter.AddToList(followList);
//                    ynfAdapter.notifyDataSetChanged();
////                    for (int i = 0; i < followList.size(); i++) {
////
////                        if (!db.CheckItem(followList.get(i).getId(), DbFollow.TB_FOLLOWING)) {
////                            fo.add(db.getOneItem(followList.get(i).getId(), DbFollow.TB_FOLLOWER));
////
////                        }
////                        if (i == followList.size() - 1) {
////                            ynfAdapter.AddToList(fo);
////                            ynfAdapter.notifyDataSetChanged();
////                        }
////                    }
//
//                }
//
//            }
//        });

        follow_all.setOnClickListener(new View.OnClickListener() {
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
                                    new AllFollow(session, ynfAdapter, getContext(), followList, followList.get(0).getId(), q).execute();
                                }
                                dialogf.dismiss();
                            } else if (r != 0) {
                                for (int q = 0; q < r - 1; q++) {
                                    new AllFollow(session, ynfAdapter, getContext(), followList, followList.get(0).getId(), q).execute();
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
                                    new AllFollow(session, ynfAdapter, getContext(), followList, followList.get(0).getId(), q).execute();
                                    if (q == 15)
                                        dialogf.dismiss();
                                }

                            } else if (r != 0) {
                                for (int q = 0; q < r - 1; q++) {
                                    new AllFollow(session, ynfAdapter, getContext(), followList, followList.get(0).getId(), q).execute();
                                    if (q == r - 2)
                                        dialogf.dismiss();
                                }

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
                            new AllFollow(session, ynfAdapter, getContext(), followList, followList.get(0).getId(), q).execute();
                        }
                        dialogf.dismiss();
                    } else if (r != 0) {
                        for (int q = 0; q < r - 1; q++) {
                            new AllFollow(session, ynfAdapter, getContext(), followList, followList.get(0).getId(), q).execute();

                        }
                        dialogf.dismiss();
                    }
                }
            }
        });


        return view;
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-6353098097853332/5531784892",
                new AdRequest.Builder().build());
    }

//    public void AllFollow(TwitterSession session1, FollowerYnfAdapter ynfAdapter, Context context, List<follow> fo, long id, int j){
//        DbFollow db;
//        db = DbFollow.getInstance(context);
//        db.getWritableDatabase();
//        MyTwitterApiClient apiClient = new MyTwitterApiClient(session1);
//        apiClient.getCustomTwitterService().CreateFollow(id).enqueue(new retrofit2.Callback() {
//            @Override
//            public void onResponse(Call call, @NonNull Response response) {
//                if (response.body() != null) {
//                    Log.i("ad", "Create follow");
//                    ynfAdapter.RemoveList(0);
//                    ynfAdapter.notifyDataSetChanged();
//                    if (fo.size() > j)
//                        db.AddItem(fo.get(j), DbFollow.TB_FOLLOWING);
//                    db.close();
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call call, Throwable t) {
//
//            }
//        });
//    }

    public static class AllFollow extends AsyncTask<Void, Void, Void> {

        TwitterSession session1;
        FollowerYnfAdapter ynfAdapter;
        DbFollow db;
        Context context;
        public int j;
        List<follow> fo;
        long id;

        public AllFollow(TwitterSession session1, FollowerYnfAdapter ynfAdapter, Context db, List<follow> fo, long id, int j) {
            this.session1 = session1;
            this.ynfAdapter = ynfAdapter;
            this.context = db;
            this.fo = fo;
            this.id = id;
            this.j = j;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            db = DbFollow.getInstance(context);
            db.getWritableDatabase();
            MyTwitterApiClient apiClient = new MyTwitterApiClient(session1);
            apiClient.getCustomTwitterService().CreateFollow(id).enqueue(new retrofit2.Callback() {
                @Override
                public void onResponse(Call call, @NonNull Response response) {
                    if (response.body() != null) {
                        Log.i("ad", "Create follow");
                        ynfAdapter.RemoveList(0);
                        ynfAdapter.notifyDataSetChanged();
                        if (fo.size() > j)
                            db.AddItem(fo.get(j), DbFollow.TB_FOLLOWING);
                        db.close();

                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {

                }
            });
            return null;
        }

//        public void follow() {
//            MyTwitterApiClient apiClient = new MyTwitterApiClient(session1);
//            apiClient.getCustomTwitterService().CreateFollow(id).enqueue(new retrofit2.Callback() {
//                @Override
//                public void onResponse(Call call, @NonNull Response response) {
//                    if (response.body() != null) {
//                        Log.i("ad", "Create follow");
//                        ynfAdapter.RemoveList(0);
//                        ynfAdapter.notifyDataSetChanged();
//                        db.AddItem(fo.get(j), DbFollow.TB_FOLLOWING);
//                        db.close();
//
//                    }
//                }
//
//                @Override
//                public void onFailure(Call call, Throwable t) {
//
//                }
//            });
//        }

    }

}
