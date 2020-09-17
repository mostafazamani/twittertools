package com.op.crush.menu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.op.crush.MainMenu;
import com.op.crush.MyTwitterApiClient;
import com.op.crush.R;
import com.op.crush.models.UserShow;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;


import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeBottomFragment extends Fragment {

    MainMenu m;
    ImageView profile , banner;
    Button follower_num , following_num ;
    SharedPreferences preferences;
    TwitterSession session;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.home_fragment, container, false);
        preferences = view.getContext().getSharedPreferences("Courser", Context.MODE_PRIVATE);
        session = TwitterCore.getInstance().getSessionManager().getActiveSession();

        banner  = view.findViewById(R.id.banner_profile);
        profile = view.findViewById(R.id.profile_image);
        follower_num = view.findViewById(R.id.follower_num);
        following_num = view.findViewById(R.id.following_num);

        user_info(session,view.getContext());

        Map<String,Object> map = new HashMap<>();
        map.put("id1",555555);
        map.put("id2",666666);
        db.collection("crush").document("user").set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("firebase","saved");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("firebase","not saved"+e.getMessage());

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
                transaction.replace(R.id.fragment_container, followerYouNotFollow ); // give your fragment container id in first parameter
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();

            }
        });

        following_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new FollowingNotFollowYou();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment );
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });


        return view;
       // return inflater.inflate(R.layout.home_fragment,container,false);
    }

    public void user_info(TwitterSession session, final Context context){

        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(session);
        myTwitterApiClient.getCustomTwitterService().User(session.getUserId() , session.getUserName()).enqueue(new Callback<UserShow>() {
            @Override
            public void onResponse(Call<UserShow> call, Response<UserShow> response) {


                if (response.body() != null) {
                    UserShow show = response.body();
                    /*Toast.makeText(HomeBottomFragment.this, ""+show.getProfile_name() + "\n"
                            +show.getProfile_image_url() + "\n" + show.getFollowers_count(), Toast.LENGTH_SHORT).show();*/
                    int cf = show.getFollowers_count()+show.getFollowings_count();
                    preferences.edit().putInt("CP",cf ).apply();

                    String purl = show.getProfile_image_url();
                    String burl = show.getProfile_banner_url();
                    follower_num.setText("Follower\n"+String.valueOf(show.getFollowers_count()));
                    following_num.setText("Following\n"+String.valueOf(show.getFollowings_count()));
                    String url = geturlpic(purl);


                    Picasso.with(context).load(burl).into(banner);
                    Picasso.with(context).load(url).into(profile);


                }


            }

            @Override
            public void onFailure(Call<UserShow> call, Throwable t) {

            }
        });

    }

    public String geturlpic(String s){
        char[] chars = s.toCharArray();
        String url="";
        for (int i = 0 ; i<chars.length-11;i++){
            url += chars[i];
        }

        url += ".jpg";

        return url;
    }

}