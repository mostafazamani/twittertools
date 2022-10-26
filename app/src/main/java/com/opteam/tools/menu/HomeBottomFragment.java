package com.opteam.tools.menu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jh.circularlist.CircularListView;
import com.jh.circularlist.CircularTouchListener;
import com.opteam.tools.MainMenu;
import com.opteam.tools.MyTwitterApiClient;
import com.opteam.tools.R;
import com.opteam.tools.Room.CircleCrush.UserCrush;
import com.opteam.tools.Room.CircleCrush.UserCrushDatabase;
import com.opteam.tools.adapter.CircularItemAdapter;
import com.opteam.tools.adapter.CrushsAdapter;
import com.opteam.tools.models.UserShow;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeBottomFragment extends Fragment {

    MainMenu m;
    ImageView profile;
    UserCrushDatabase database;
    SharedPreferences preferences;
    TwitterSession session;
    FloatingActionButton searchFAB;

    private String[] colors = {"#123456 , #654321 , #908765,#142524"};
    int size;
    public FirebaseFirestore firestore;
    private CollectionReference collectionReference;
    private List<DocumentSnapshot> querySnapshots;
    private CircularItemAdapter adapter;
    public TextView txt_crushs;
    ListView list_crushs;
    public Button btn_crushs;
    public Button btn_open_nav;
    ArrayList<Bitmap> itemTitles;
    ProgressDialog dialog;
    int step = 0;

    public Context context;
    private RewardedVideoAd mRewardedVideoAd;
    boolean adl = false;
    private SharedPreferences preferences1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.home_fragment, container, false);

        MobileAds.initialize(view.getContext());
        AdView adView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();

        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.i("adbanner","clicked");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Log.i("adbanner","closed");
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                Log.i("adbanner",adError.getMessage());
            }

            @Override
            public void onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
                Log.i("adbanner","impression");
            }

            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.i("adbanner","Loaded");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.i("adbanner","opened");
            }
        });


        Twitter.initialize(view.getContext());
        preferences = view.getContext().getSharedPreferences("Courser", Context.MODE_PRIVATE);
        session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().build();

        firestore.setFirestoreSettings(settings);

        database = UserCrushDatabase.getInstance(view.getContext());

        context = view.getContext();

        profile = view.findViewById(R.id.profile_image);
        searchFAB = view.findViewById(R.id.search_fab);


        txt_crushs = view.findViewById(R.id.txt_crushs);
        list_crushs = view.findViewById(R.id.crushs);
        btn_crushs = view.findViewById(R.id.btn_refresh_crushs);
//        btn_open_nav = view.findViewById(R.id.btn_open_nav);

        dialog = new ProgressDialog(view.getContext());
        dialog.setMessage("wait...");
        dialog.setCancelable(false);

        user_info(session, view.getContext());

        MobileAds.initialize(view.getContext(), "ca-app-pub-6353098097853332~3028901753");
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(view.getContext());

        preferences1 = view.getContext().getSharedPreferences("AdL", Context.MODE_PRIVATE);
        String a = preferences1.getString("ad", "true");
        if (a.equals("true"))
            adl = true;


        itemTitles = new ArrayList<>();
//        itemTitles.add(BitmapFactory.decodeResource(getResources(), R.drawable.avatar));
        // usage sample
        final CircularListView circularListView = view.findViewById(R.id.my_circular_list);
        adapter = new CircularItemAdapter(getLayoutInflater(), itemTitles);
        circularListView.setAdapter(adapter);
        circularListView.setRadius(100);
        circularListView.setOnItemClickListener(new CircularTouchListener.CircularItemClickListener() {
            @Override
            public void onItemClick(View view, int index) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:

                                // new Remove(database, session).execute(index);

                                if (database.userCrushDao().getUserCrush().size() > 0) {
                                    Log.i("removeItem", String.valueOf(database.userCrushDao().getUserCrush().get(index + 1).getUser_id()));
                                    firestore.collection("crush")
                                            .document(String.valueOf(database.userCrushDao().getUserCrush().get(index + 1).getUser_id()))
                                            .update(String.valueOf(session.getId()), FieldValue.delete()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            database.userCrushDao().delete(database.userCrushDao().getUserCrush().get(index + 1));
                                            Log.i("removeItem", "remove from fire base");
                                            adapter.removeItemAt(index);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.i("removeItem", e.getMessage());
                                        }
                                    });

                                }


                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete this item from crush list?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();


            }
        });
        new ListOfCrush(view.getContext(), database, session, adapter, getLayoutInflater()).execute();


        btn_crushs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                if (adl) {
                    mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
                        @Override
                        public void onRewardedVideoAdLoaded() {
                            mRewardedVideoAd.show();
                        }

                        @Override
                        public void onRewardedVideoAdOpened() {

                        }

                        @Override
                        public void onRewardedVideoStarted() {

                        }

                        @Override
                        public void onRewardedVideoAdClosed() {
//                            Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                        @Override
                        public void onRewarded(RewardItem rewardItem) {
                            ListCrushs(database, session, firestore, view.getContext());
                        }

                        @Override
                        public void onRewardedVideoAdLeftApplication() {
                            Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                        @Override
                        public void onRewardedVideoAdFailedToLoad(int i) {
                            ListCrushs(database, session, firestore, view.getContext());
                        }

                        @Override
                        public void onRewardedVideoCompleted() {
                            ListCrushs(database, session, firestore, view.getContext());
                        }
                    });

                    loadRewardedVideoAd();
                } else
                    ListCrushs(database, session, firestore, view.getContext());
            }
        });


//        Button button = view.findViewById(R.id.add_btn);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Long[] strin = {3095002318L, 897347999537483776L, 1241093018947588097L, 1315701730798194688L,
//                        1087319947754258433L, 1287308424137539584L, 1293861594355695616L, 1300485434078830592L};
////                for (Long s : strin)
//                if (step < 8)
//                    lod_circle(strin[step]);
//                step++;
//            }
//        });

        Button button1 = view.findViewById(R.id.remove);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DeleteAll(database).execute();
            }
        });


/*
        following_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new FollowingNotFollowYou();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });*/
        searchFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           /* Fragment fragment = new CrushSearch();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();*/

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                DialogFragment dialogFragment = new CrushSearch(adapter, getLayoutInflater());
                dialogFragment.show(ft, "dialog");
            }
        });
        // return inflater.inflate(R.layout.home_fragment,container,false);


        return view;
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-6353098097853332/5531784892",
                new AdRequest.Builder().build());
    }

    public void read_crushs() {
        collectionReference = firestore.collection("crush").
                document(String.valueOf(session.getId())).collection("cr");

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                querySnapshots = queryDocumentSnapshots.getDocuments();
                size = querySnapshots.size();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void lod_circle(long url) {

        new Ins(database).execute(new UserCrush(url));

        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(session);
        myTwitterApiClient.getCustomTwitterService().SeeUserInfo(url).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.body() != null) {


                    try {
                        JsonArray elements = (JsonArray) response.body();


                        JsonObject jsonObject = (JsonObject) elements.get(0);
                        JsonElement f = jsonObject.get("profile_image_url");
                        Picasso.with(getContext()).load(jsonObject.get("profile_image_url").getAsString()).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                View v = getLayoutInflater().inflate(R.layout.circular_adapter, null);
                                ImageView itemView = v.findViewById(R.id.img_item);
                                itemView.setImageBitmap(bitmap);
                                Log.i("ciecle_list", "bit");
                                adapter.addItem(v);

                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });


                    } catch (Exception e) {
                        Toast.makeText(getContext(), "seeUserInfo :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

            }
        });


    }


    public void user_info(TwitterSession session, final Context context) {

        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(session);
        myTwitterApiClient.getCustomTwitterService().User(session.getUserId(), session.getUserName()).enqueue(new Callback<UserShow>() {
            @Override
            public void onResponse(Call<UserShow> call, Response<UserShow> response) {


                if (response.body() != null) {
                    UserShow show = response.body();
                    /*Toast.makeText(HomeBottomFragment.this, ""+show.getProfile_name() + "\n"
                            +show.getProfile_image_url() + "\n" + show.getFollowers_count(), Toast.LENGTH_SHORT).show();*/
                    int cf = show.getFollowers_count() + show.getFollowings_count();
                    // preferences.edit().putInt("CP", cf).apply();


                    new CountDownTimer(5000, 1000) {
                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {

//                            Constraints constraints = new Constraints.Builder()
//                                    .setRequiresCharging(true)
//                                    .build();
//
//                            PeriodicWorkRequest workRequest = new PeriodicWorkRequest.
//                                    Builder(LoadFollowing.class, 1, TimeUnit.MINUTES)
//                                    .setConstraints(constraints).build();
//
//                            PeriodicWorkRequest workRequest1 = new PeriodicWorkRequest.
//                                    Builder(LoadFollower.class, 1, TimeUnit.MINUTES)
//                                    .setConstraints(constraints).build();
//
//
//                            WorkManager.getInstance(context).enqueueUniquePeriodicWork("following",
//                                    ExistingPeriodicWorkPolicy.REPLACE,
//                                    workRequest);
//
//                            WorkManager.getInstance(context).enqueueUniquePeriodicWork("follower",
//                                    ExistingPeriodicWorkPolicy.REPLACE,
//                                    workRequest1);
//

                        }
                    }.start();
                    String purl = show.getProfile_image_url();
                    String url = geturlpic(purl);
                    Picasso.with(context).load(url).into(profile);


                }


            }

            @Override
            public void onFailure(Call<UserShow> call, Throwable t) {

            }
        });

    }

    public String geturlpic(String s) {
        char[] chars = s.toCharArray();
        String url = "";
        for (int i = 0; i < chars.length - 11; i++) {
            url += chars[i];
        }

        url += ".jpg";

        return url;
    }

    public void ListCrushs(UserCrushDatabase database, TwitterSession session, FirebaseFirestore firestore
            , Context context) {
        List<UserCrush> list = database.userCrushDao().getUserCrush();
        firestore.collection("crush").document(String.valueOf(session.getUserId()))
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                dialog.dismiss();
                if (documentSnapshot.getData() != null) {
                    Set<String> ma = documentSnapshot.getData().keySet();
                    Log.i("list", ma.toString());
                    Log.i("list", list.toString());
                    if (ma.size() > 0) {
                        List<Long> list1 = new ArrayList<>();
                        List<String> stringList = new ArrayList<>(ma);
                        for (int i = 0; i < stringList.size(); i++) {
                            for (int j = 0; j < list.size(); j++) {
                                Log.i("crushslist", String.valueOf(list.get(j).getUser_id()));
                                Log.i("crushslist", stringList.get(i));
                                if (stringList.get(i).equals(String.valueOf(list.get(j).getUser_id()))) {
                                    list1.add(list.get(j).getUser_id());
                                }
                            }
                        }

                        CrushsAdapter crushsAdapter = new CrushsAdapter(context);
                        list_crushs.setAdapter(crushsAdapter);
                        if (list1.size() < 1)
                            Toast.makeText(context, "No one found", Toast.LENGTH_SHORT).show();
                        crushsAdapter.AddToList(list1);
                    }
                } else {
                    Toast.makeText(context, "No one found", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Log.i("crushslist", "onFailure");
            }
        });
    }

    public static class ListOfCrush extends AsyncTask<Void, Void, Void> {

        Context context;
        UserCrushDatabase database;
        TwitterSession session;
        LayoutInflater minf;
        CircularItemAdapter adapter;

        public ListOfCrush(Context context, UserCrushDatabase database, TwitterSession session,
                           CircularItemAdapter adapter, LayoutInflater minf) {
            this.context = context;
            this.database = database;
            this.session = session;
            this.adapter = adapter;
            this.minf = minf;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            f_circle();
            return null;
        }

        public void f_circle() {

            List<UserCrush> list = database.userCrushDao().getUserCrush();

            if (list.size() > 0)
                for (int i = 0; i < list.size(); i++) {

                    MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(session);
                    myTwitterApiClient.getCustomTwitterService().SeeUserInfo(list.get(i).getUser_id()).enqueue(new Callback<JsonArray>() {
                        @Override
                        public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                            if (response.body() != null) {


                                try {
                                    JsonArray elements = (JsonArray) response.body();


                                    JsonObject jsonObject = (JsonObject) elements.get(0);
                                    View v = minf.inflate(R.layout.circular_adapter, null);
                                    ImageView itemView = v.findViewById(R.id.img_item);
                                    Picasso.with(context).load(jsonObject.get("profile_image_url").getAsString()).into(itemView);
                                    Log.i("ciecle_list", "bit");
                                    adapter.addItem(v);


                                } catch (Exception e) {
                                    Toast.makeText(context, "seeUserInfo :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }

                        }

                        @Override
                        public void onFailure(Call<JsonArray> call, Throwable t) {

                        }
                    });


                }

        }

    }

    public static class DeleteAll extends AsyncTask<Void, Void, Void> {
        UserCrushDatabase database;

        public DeleteAll(UserCrushDatabase database) {
            this.database = database;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            database.userCrushDao().deleteAll();
            return null;
        }
    }

    public static class Ins extends AsyncTask<UserCrush, Void, Void> {
        UserCrushDatabase database;

        public Ins(UserCrushDatabase database) {
            this.database = database;
        }

        @Override
        protected Void doInBackground(UserCrush... userCrushes) {
            database.userCrushDao().insert(userCrushes[0]);
            return null;
        }
    }

    public static class Remove extends AsyncTask<Integer, Void, Void> {
        UserCrushDatabase database;
        public FirebaseFirestore firestore;
        TwitterSession session;

        public Remove(UserCrushDatabase database, TwitterSession session) {
            this.database = database;
            this.session = session;
            firestore = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().build();
            firestore.setFirestoreSettings(settings);
        }


        @Override
        protected Void doInBackground(Integer... integers) {

            if (integers[0] != null) {
                database.userCrushDao().delete(database.userCrushDao().getUserCrush().get(integers[0]));
                firestore.collection("crush")
                        .document(String.valueOf(database.userCrushDao().getUserCrush().get(integers[0]).getId()))
                        .update(String.valueOf(session.getId()), FieldValue.delete()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("removeItem", "remove from fire base");
                    }
                });

            }
            return null;
        }
    }

}