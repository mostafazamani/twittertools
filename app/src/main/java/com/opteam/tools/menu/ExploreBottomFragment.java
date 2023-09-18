package com.opteam.tools.menu;

//import android.support.v4.app.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.opteam.tools.MyTwitterApiClient;
import com.opteam.tools.R;
import com.opteam.tools.adapter.ExploreAdapter;
import com.opteam.tools.adapter.homeTimeline;
import com.opteam.tools.models.SuggestUser;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExploreBottomFragment extends Fragment {
    homeTimeline adapter;
    private TwitterSession session;

    int j = 0;
    List<SuggestUser> sl;

    Button add_to_sugest;

    //DbSuggest dbSuggest;
    EditText editText;
    SwipeRefreshLayout refreshLayout;
    String countryCode;
    GridView gridView;
    private ExploreAdapter exploreAdapter;
    private FirebaseFirestore firestore;
    boolean conn = true;
    private ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Twitter.initialize(getContext());
        final View view = inflater.inflate(R.layout.explore_fragment, container, false);


        MobileAds.initialize(view.getContext());
        AdView adView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();

        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.i("adbanner", "clicked");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Log.i("adbanner", "closed");
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                Log.i("adbanner", adError.getMessage());
            }

            @Override
            public void onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
                Log.i("adbanner", "impression");
            }

            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.i("adbanner", "Loaded");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.i("adbanner", "opened");
            }
        });


        session = TwitterCore.getInstance().getSessionManager().getActiveSession();

        refreshLayout = view.findViewById(R.id.ex_swip);
        refreshLayout.setEnabled(false);

        firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().build();
        firestore.setFirestoreSettings(settings);

        List<SuggestUser> suggestUsers = new ArrayList<>();

        TelephonyManager telephoneManager = (TelephonyManager) view.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        countryCode = telephoneManager.getNetworkCountryIso();

        if (countryCode == null)
            countryCode = "Public";
        else if (countryCode.trim().toLowerCase().equals("gb") || countryCode.trim().toLowerCase().equals("de") || countryCode.trim().toLowerCase().equals("tr") || countryCode.trim().toLowerCase().equals("fr"))
            countryCode = "FR";
        else if (countryCode.trim().toLowerCase().equals("us") || countryCode.trim().toLowerCase().equals("zm") || countryCode.trim().toLowerCase().equals("ca"))
            countryCode = "SA";
        else if (countryCode.trim().toLowerCase().equals("ir"))
            countryCode = "Public";
        else
            countryCode = "Public";

        Log.i("countryCode", countryCode);

        // dbSuggest = new DbSuggest(view.getContext());
        //  dbSuggest.getReadableDatabase();


        //  sl = dbSuggest.getItem();

        dialog = new ProgressDialog(view.getContext());
        dialog.setMessage("loading...");
        dialog.setCancelable(false);


        ////////////////GridView//////////////////////////
        gridView = view.findViewById(R.id.gridview);
        exploreAdapter = new ExploreAdapter(view.getContext());
        gridView.setAdapter(exploreAdapter);

        dialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (countryCode != null) {
                    firestore.collection("SugestUser").document(countryCode).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {

                                for (int i = 0; i < 100; i++) {
                                    int rand = new Random().nextInt(19000);
                                    String id = documentSnapshot.getString(String.valueOf(rand));

                                    MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(session);
                                    if (id != null)
                                        myTwitterApiClient.getCustomTwitterService().SeeUserInfo(Long.parseLong(id)).enqueue(new Callback<JsonArray>() {
                                            @Override
                                            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                                                if (response.body() != null) {


                                                    try {
                                                        JsonArray elements = (JsonArray) response.body();
                                                        SuggestUser suggestUser = new SuggestUser();

                                                        JsonObject jsonObject = (JsonObject) elements.get(0);


                                                        suggestUser.setId(Long.parseLong(id));
                                                        suggestUser.setName(jsonObject.get("name").getAsString());
                                                        suggestUser.setScreenName(jsonObject.get("screen_name").getAsString());
                                                        suggestUser.setProfilePictureUrl(jsonObject.get("profile_image_url").getAsString());

                                                        suggestUsers.add(suggestUser);

                                                        exploreAdapter.AddToList(suggestUser);
                                                        exploreAdapter.notifyDataSetChanged();
                                                        dialog.dismiss();
                                                    } catch (Exception e) {
                                                        dialog.dismiss();
                                                    }

                                                }
                                                dialog.dismiss();

                                            }

                                            @Override
                                            public void onFailure(Call<JsonArray> call, Throwable t) {
                                                dialog.dismiss();
                                                if (conn) {
                                                    Toast.makeText(view.getContext(), "check your connection", Toast.LENGTH_SHORT).show();
                                                    conn = false;
                                                }
                                            }
                                        });


                                }

                            }
                            refreshLayout.setEnabled(true);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            refreshLayout.setEnabled(true);
                            dialog.dismiss();
                            Toast.makeText(view.getContext(), "Failed, pull to refresh", Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    dialog.dismiss();
                }
            }
        }, 1000);


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

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setEnabled(true);

                TelephonyManager telephoneManager = (TelephonyManager) view.getContext().getSystemService(Context.TELEPHONY_SERVICE);
                countryCode = telephoneManager.getNetworkCountryIso();

                if (countryCode == null)
                    countryCode = "Public";
                else if (countryCode.trim().toLowerCase().equals("gb") || countryCode.trim().toLowerCase().equals("de") || countryCode.trim().toLowerCase().equals("tr") || countryCode.trim().toLowerCase().equals("fr"))
                    countryCode = "FR";
                else if (countryCode.trim().toLowerCase().equals("us") || countryCode.trim().toLowerCase().equals("zm") || countryCode.trim().toLowerCase().equals("ca"))
                    countryCode = "SA";
                else if (countryCode.trim().toLowerCase().equals("ir"))
                    countryCode = "Public";
                else
                    countryCode = "Public";

                Log.i("countryCode", countryCode);

                conn = true;
                dialog.show();
                if (countryCode != null) {
                    firestore.collection("SugestUser").document(countryCode).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {

                                for (int i = 0; i < 100; i++) {
                                    int rand = new Random().nextInt(19000);
                                    String id = documentSnapshot.getString(String.valueOf(rand));

                                    MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(session);
                                    if (id != null)
                                        myTwitterApiClient.getCustomTwitterService().SeeUserInfo(Long.parseLong(id)).enqueue(new Callback<JsonArray>() {
                                            @Override
                                            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                                                if (response.body() != null) {


                                                    try {
                                                        JsonArray elements = (JsonArray) response.body();
                                                        SuggestUser suggestUser = new SuggestUser();

                                                        JsonObject jsonObject = (JsonObject) elements.get(0);


                                                        suggestUser.setId(Long.parseLong(id));
                                                        suggestUser.setName(jsonObject.get("name").getAsString());
                                                        suggestUser.setScreenName(jsonObject.get("screen_name").getAsString());
                                                        suggestUser.setProfilePictureUrl(jsonObject.get("profile_image_url").getAsString());

                                                        suggestUsers.add(suggestUser);

                                                        exploreAdapter.AddToList(suggestUser);
                                                        exploreAdapter.notifyDataSetChanged();
                                                        dialog.dismiss();
                                                    } catch (Exception e) {

                                                    }

                                                }

                                            }

                                            @Override
                                            public void onFailure(Call<JsonArray> call, Throwable t) {
                                                dialog.dismiss();
                                                if (conn) {
                                                    Toast.makeText(view.getContext(), "check your connection", Toast.LENGTH_SHORT).show();
                                                    conn = false;
                                                }
                                            }
                                        });


                                }

                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(view.getContext(), "Failed, pull to refresh", Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    dialog.dismiss();
                }


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
                refreshLayout.setRefreshing(false);
            }

        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dialog.dismiss();
    }

}
