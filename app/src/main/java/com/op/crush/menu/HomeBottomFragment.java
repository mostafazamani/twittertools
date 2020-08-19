package com.op.crush.menu;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.op.crush.MainMenu;
import com.op.crush.MyTwitterApiClient;
import com.op.crush.R;
import com.op.crush.models.UserShow;
import com.op.crush.models.unfollowFind;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeBottomFragment extends Fragment {

    MainMenu m;
    ImageView profile , banner;
    Button follower_num , following_num ;

    private TwitterSession session;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.home_fragment, container, false);

        session = TwitterCore.getInstance().getSessionManager().getActiveSession();

        banner  = view.findViewById(R.id.banner_profile);
        profile = view.findViewById(R.id.profile_image);
        follower_num = view.findViewById(R.id.follower_num);
        following_num = view.findViewById(R.id.following_num);

        user_info(session,view.getContext());


        MyTwitterApiClient twitterApiClient = new MyTwitterApiClient(session);
        twitterApiClient.getCustomTwitterService().Unfollow().enqueue(new Callback<unfollowFind>() {
            @Override
            public void onResponse(Call<unfollowFind> call, Response<unfollowFind> response) {
                if (response.body() != null) {
                    unfollowFind find = response.body();
                    Toast.makeText(view.getContext(), ""+find.getId(), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<unfollowFind> call, Throwable t) {

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
                transaction.replace(R.id.fragment_container, followerYouNotFollow ); // give your fragment container id in first parameter
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
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