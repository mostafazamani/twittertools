package com.op.crush.menu;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import com.google.firebase.firestore.QuerySnapshot;
import com.jh.circularlist.CircularListView;
import com.jh.circularlist.CircularTouchListener;
import com.op.crush.MainMenu;
import com.op.crush.MyTwitterApiClient;
import com.op.crush.R;
import com.op.crush.adapter.CircularItemAdapter;
import com.op.crush.models.UserShow;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeBottomFragment extends Fragment {

    MainMenu m;
    ImageView profile, banner;
    Button follower_num, following_num;
    SharedPreferences preferences;
    TwitterSession session;

    private String[] colors = {"#123456 , #654321 , #908765,#142524"};
    int size;
    public FirebaseFirestore firestore;
    private CollectionReference collectionReference;
    private List<DocumentSnapshot> querySnapshots;
    private CircularItemAdapter adapter;


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

        TelephonyManager telephoneManager = (TelephonyManager) view.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = telephoneManager.getNetworkCountryIso();

        banner = view.findViewById(R.id.banner_profile);
        profile = view.findViewById(R.id.profile_image);
        follower_num = view.findViewById(R.id.follower_num);
        following_num = view.findViewById(R.id.following_num);


        user_info(session, view.getContext());


        ArrayList<Bitmap> itemTitles = new ArrayList<>();
//        for(int i = 0 ; i < 6 ; i ++){
//            itemTitles.add(String.valueOf(i));
//        }
        itemTitles.add(BitmapFactory.decodeResource(getResources(), R.drawable.avatar));


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
            }
        });


        Map<String, Object> map = new HashMap<>();
        map.put("id1", countryCode);

        add_crush(map);
        read_crushs();
        Button button = view.findViewById(R.id.add_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] strin = {"https://i.stack.imgur.com/vgGRo.png","https://i.stack.imgur.com/vgGRo.png",
                        "https://i.stack.imgur.com/vgGRo.png","https://i.stack.imgur.com/vgGRo.png"};
                lod_circle(strin, 0);

            }
        });


        follower_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*NextFragment nextFrag= new NextFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.Layout_container, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();*/

                Fragment followerYouNotFollow = new FollowerYouNotFollow();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                //FragmentManager.beginTransaction().add(R.id.fragment_container, fragment2, "2").hide(fragment2).commit();
                transaction.replace(R.id.fragment_container, followerYouNotFollow); // give your fragment container id in first parameter
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();

            }
        });

        following_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new FollowingNotFollowYou();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });
        // return inflater.inflate(R.layout.home_fragment,container,false);


        return view;
    }


    public void add_crush(Map<String, Object> map) {
        firestore.collection("crush")
                .document(String.valueOf(session.getId())).collection("cr").document().set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("firebase", "saved");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("firebase", "not saved" + e.getMessage());

            }
        });
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

    public void lod_circle(String[] url, int f) {

        Picasso.with(getContext()).load(url[f]).into(new Target() {
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
                    String burl = show.getProfile_banner_url();
                    follower_num.setText("Follower\n" + String.valueOf(show.getFollowers_count()));
                    following_num.setText("Following\n" + String.valueOf(show.getFollowings_count()));
                    String url = geturlpic(purl);


                    Picasso.with(context).load(burl).into(banner);
                    RequestCreator d = Picasso.with(context).load(url);
                    d.into(profile);
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

}