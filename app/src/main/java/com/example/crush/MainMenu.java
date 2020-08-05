package com.example.crush;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crush.menu.ExploreBottomFragment;
import com.example.crush.menu.HomeBottomFragment;
import com.example.crush.menu.TwittsBottomFragment;
import com.example.crush.models.UserShow;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainMenu extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private TwitterSession session;

    ImageView profile , banner;
    TextView follower_num , following_num , twitts_num;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Twitter.initialize(this);
        setContentView(R.layout.content_main);

       /* banner = findViewById(R.id.banner_profile);
        profile = findViewById(R.id.profile_image);
        follower_num =findViewById(R.id.follower_num);
        following_num = findViewById(R.id.following_num);
        twitts_num = findViewById(R.id.twitt_num);*/
        bottomNavigationView = findViewById(R.id.bottom_nav);



        session = TwitterCore.getInstance().getSessionManager().getActiveSession();

        bottomNavigationView.setOnNavigationItemSelectedListener(bottomListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeBottomFragment()).commit();

        //user_info(session);

    }



/*
    public void user_info(TwitterSession session){

        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(session);
        myTwitterApiClient.getCustomTwitterService().User(session.getUserId() , session.getUserName()).enqueue(new Callback<UserShow>() {
            @Override
            public void onResponse(Call<UserShow> call, Response<UserShow> response) {

                if (response.body() != null) {
                    UserShow show = response.body();
                    Toast.makeText(MainMenu.this, ""+show.getProfile_name() + "\n"
                            +show.getProfile_image_url() + "\n" + show.getFollowers_count(), Toast.LENGTH_SHORT).show();

                    String purl = show.getProfile_image_url();
                    String burl = show.getProfile_banner_url();
                    follower_num.setText(String.valueOf(show.getFollowers_count()));
                    following_num.setText(String.valueOf(show.getFollowings_count()));

                    new DownloadImageTask((ImageView) findViewById(R.id.banner_profile))
                            .execute(burl);

                    new DownloadImageTask((ImageView) findViewById(R.id.profile_image))
                            .execute(purl);

                }


            }

            @Override
            public void onFailure(Call<UserShow> call, Throwable t) {

            }
        });

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bannerImage ;

        public DownloadImageTask(ImageView bannerImage ) {
            this.bannerImage = bannerImage;
         //   this.profileImage = profilImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("خطا در بارگیری عکس", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bannerImage.setImageBitmap(result);
            //profileImage.setImageBitmap(result);
        }
    }*/

    private BottomNavigationView.OnNavigationItemSelectedListener bottomListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;
                    switch (menuItem.getItemId()) {
                        case R.id.item_1:
                            selectedFragment = new HomeBottomFragment();
                            break;
                        case R.id.item_2:
                            selectedFragment = new ExploreBottomFragment();
                            break;
                        case R.id.item_3:
                            selectedFragment = new TwittsBottomFragment();
                            break;


                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    return true;
                }
            };
}