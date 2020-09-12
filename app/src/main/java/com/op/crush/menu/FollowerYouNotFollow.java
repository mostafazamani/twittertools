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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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

    int j = 0;


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

        session = TwitterCore.getInstance().getSessionManager().getActiveSession();


        MobileAds.initialize(view.getContext(), "ca-app-pub-6353098097853332~3028901753");
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(view.getContext());




//        List<String> testDeviceIds = Arrays.asList("33BE2250B43518CCDA7DE426D04EE231");
//        RequestConfiguration configuration =
//                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
//        MobileAds.setRequestConfiguration(configuration);


        model = new ViewModelProvider(getActivity()).get(ProgressViewModel.class);
        model.getState().observe(getViewLifecycleOwner(), new Observer<List<ProgressState>>() {
            @Override
            public void onChanged(List<ProgressState> progressStates) {
                stat = progressStates.get(progressStates.size() - 1).getState();
                progressBar.setProgress(stat);
                txtProgress.setText(String.valueOf(stat) + "%");
                if (stat == 100) {
                    progressBar.setVisibility(View.INVISIBLE);
                    ynfAdapter = new FollowerYnfAdapter(view.getContext());
                    list.setAdapter(ynfAdapter);

                    db = DbFollow.getInstance(view.getContext());


                    fo = new ArrayList<>();

                    followList = db.getItem(DbFollow.TB_FOLLOWER);

                    for (int i = 0; i < followList.size(); i++) {

                        if (!db.CheckItem(followList.get(i).getId(), DbFollow.TB_FOLLOWING)) {
                            fo.add(db.getOneItem(followList.get(i).getId(), DbFollow.TB_FOLLOWER));

                        }
                        if (i == followList.size() - 1) {
                            ynfAdapter.AddToList(fo);
                            ynfAdapter.notifyDataSetChanged();
                        }
                    }

                }

            }
        });

        follow_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



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

                        new AllFollow(session, ynfAdapter, db, fo).execute();
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

    public static class AllFollow extends AsyncTask<Void, Void, Void> {

        TwitterSession session1;
        FollowerYnfAdapter ynfAdapter;
        DbFollow db;
        public int j;
        List<follow> fo;

        public AllFollow(TwitterSession session1, FollowerYnfAdapter ynfAdapter, DbFollow db, List<follow> fo) {
            this.session1 = session1;
            this.ynfAdapter = ynfAdapter;
            this.db = db;
            this.fo = fo;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            j = 0;
            follow();

            return null;
        }

        public void follow() {
            MyTwitterApiClient apiClient = new MyTwitterApiClient(session1);
            apiClient.getCustomTwitterService().CreateFollow(fo.get(j).getId()).enqueue(new retrofit2.Callback() {
                @Override
                public void onResponse(Call call, @NonNull Response response) {
                    if (response.body() != null) {
                        Log.i("ad", "Create follow");
                        ynfAdapter.RemoveList(0);
                        ynfAdapter.notifyDataSetChanged();
                        db.AddItem(fo.get(j), DbFollow.TB_FOLLOWING);
                        db.close();
                        j += 1;
                        if (j <= 1)
                            follow();
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {


                }
            });
        }

    }

}
