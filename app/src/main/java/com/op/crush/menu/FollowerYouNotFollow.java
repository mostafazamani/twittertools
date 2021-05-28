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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.gson.JsonObject;
import com.mopub.common.MoPub;
import com.mopub.common.MoPubReward;
import com.mopub.common.SdkConfiguration;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubRewardedVideoListener;
import com.mopub.mobileads.MoPubRewardedVideos;
import com.op.crush.DbFollow;
import com.op.crush.MyTwitterApiClient;
import com.op.crush.R;
import com.op.crush.Room.ProgressState;
import com.op.crush.Room.ProgressViewModel;
import com.op.crush.adapter.FolloweingNfyAdapter;
import com.op.crush.adapter.FollowerYnfAdapter;
import com.op.crush.models.follow;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mopub.common.logging.MoPubLog.LogLevel.INFO;

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
    private TwitterSession session;
    private RewardedVideoAd mRewardedVideoAd;
    SwipeRefreshLayout refreshLayout;
    SharedPreferences preferences;
    int j = 0;
    ProgressDialog dialogf;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.follower_ynf_fragment, container, false);

        // ((MainMenu)getActivity()).getSupportActionBar().hide();//Toolbar hidden


        follow_all = view.findViewById(R.id.follow_all);
        list = view.findViewById(R.id.list_fynf);
        txtProgress = view.findViewById(R.id.txtProgress);
        progressBar = view.findViewById(R.id.progressBar);

        refreshLayout = view.findViewById(R.id.swipy);

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

        if (preferences.getInt("FollowerCount", 0) == 1 && preferences.getInt("FollowingCount", 0) == 1) {
            progressBar.setVisibility(View.INVISIBLE);
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
                txtProgress.setText(String.valueOf(stat) + "%");
                if (stat == 100) {
                    x[0] += 1;

                    if (preferences.getInt("FollowerCount", 0) == 1 && preferences.getInt("FollowingCount", 0) == 1) {
                        progressBar.setVisibility(View.INVISIBLE);
                        txtProgress.setVisibility(View.INVISIBLE);
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
