package com.op.crush.menu;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import android.telephony.TelephonyManager;
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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jh.circularlist.CircularListView;
import com.jh.circularlist.CircularTouchListener;
import com.op.crush.MainMenu;
import com.op.crush.MyTwitterApiClient;
import com.op.crush.R;
import com.op.crush.Room.CircleCrush.UserCrush;
import com.op.crush.Room.CircleCrush.UserCrushDatabase;
import com.op.crush.adapter.CircularItemAdapter;
import com.op.crush.adapter.CrushsAdapter;
import com.op.crush.models.UserShow;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;


import java.util.ArrayList;
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
    ArrayList<Bitmap> itemTitles;
    int step = 0;

    public Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.home_fragment, container, false);
        preferences = view.getContext().getSharedPreferences("Courser", Context.MODE_PRIVATE);
        session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);

        database = UserCrushDatabase.getInstance(view.getContext());

        TelephonyManager telephoneManager = (TelephonyManager) view.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = telephoneManager.getNetworkCountryIso();

        context = view.getContext();

        profile = view.findViewById(R.id.profile_image);
        searchFAB = view.findViewById(R.id.search_fab);


        txt_crushs = view.findViewById(R.id.txt_crushs);
        list_crushs = view.findViewById(R.id.crushs);
        btn_crushs = view.findViewById(R.id.btn_refresh_crushs);



        user_info(session, view.getContext());


        itemTitles = new ArrayList<>();
        // usage sample
        final CircularListView circularListView = view.findViewById(R.id.my_circular_list);
        adapter = new CircularItemAdapter(getLayoutInflater(), itemTitles);
        circularListView.setAdapter(adapter);
        circularListView.setRadius(100);
        circularListView.setOnItemClickListener(new CircularTouchListener.CircularItemClickListener() {
            @Override
            public void onItemClick(View view, int index) {
                Toast.makeText(view.getContext(),
                        "view at index " + index + " is clicked!",
                        Toast.LENGTH_SHORT).show();
                new Remove(database).execute(index);
                adapter.removeItemAt(index);

            }
        });
        new ListOfCrush(view.getContext(), database, session, adapter).execute();


        btn_crushs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            new ListCrushs(database,session,firestore,view.getContext()).execute();
            btn_crushs.setVisibility(View.GONE);
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
            DialogFragment dialogFragment = new CrushSearch(adapter,getLayoutInflater());
            dialogFragment.show(ft, "dialog");
        }
    });
        // return inflater.inflate(R.layout.home_fragment,container,false);


        return view;
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
                    preferences.edit().putInt("CP", cf).apply();

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


    public class ListCrushs extends AsyncTask<Void,Void,Void>{
        UserCrushDatabase database;

        TwitterSession session;
        FirebaseFirestore firestore;
        private List<DocumentSnapshot> qs;
        Context context;

        public ListCrushs(UserCrushDatabase database,TwitterSession session,FirebaseFirestore firestore
                ,Context context) {
            this.database = database;
            this.session = session;
            this.firestore = firestore;
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            List<UserCrush> list = database.userCrushDao().getUserCrush();
           firestore.collection("crush").document(String.valueOf(session.getId()))
                   .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
               @Override
               public void onSuccess(DocumentSnapshot documentSnapshot) {
                   Set<String> ma = documentSnapshot.getData().keySet();
                        List<Long> list1 = new ArrayList<>();
                        List<String> stringList = new ArrayList<>();
                   for (String s:ma) {
                       stringList.add(s);
                   }
                   for (int i = 0 ; i <stringList.size();i++) {
                       for (int j = 0 ; j < list.size() ; j++) {
                           if (stringList.get(i).equals(String.valueOf(list.get(j).getUser_id()))){
                               list1.add(list.get(j).getUser_id());
                           }
                       }
                   }
                   Log.i("crushslist", String.valueOf(list1.size()));
                   Log.i("crushslist", String.valueOf(stringList.size()));

                   CrushsAdapter crushsAdapter = new CrushsAdapter(list1,context);
                   list_crushs.setAdapter(crushsAdapter);


               }
           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   Log.i("crushslist", "onFailure");
               }
           });
            return null;
        }
    }

    public class ListOfCrush extends AsyncTask<Void, Void, Void> {

        Context context;
        UserCrushDatabase database;
        TwitterSession session;

        CircularItemAdapter adapter;

        public ListOfCrush(Context context, UserCrushDatabase database, TwitterSession session, CircularItemAdapter adapter) {
            this.context = context;
            this.database = database;
            this.session = session;
            this.adapter = adapter;
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
                                    JsonElement f = jsonObject.get("profile_image_url");
                                    Picasso.with(context).load(jsonObject.get("profile_image_url").getAsString()).into(new Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                            Picasso.with(context).load(jsonObject.get("profile_image_url").getAsString()).into(new Target() {

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


                                        }

                                        @Override
                                        public void onBitmapFailed(Drawable errorDrawable) {

                                        }

                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                                        }
                                    });


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

        public Remove(UserCrushDatabase database) {
            this.database = database;
        }


        @Override
        protected Void doInBackground(Integer... integers) {
            database.userCrushDao().delete(database.userCrushDao().getUserCrush().get(integers[0]));
            Log.i("removeItem", String.valueOf(database.userCrushDao().getUserCrush().size()));

            return null;
        }
    }

}