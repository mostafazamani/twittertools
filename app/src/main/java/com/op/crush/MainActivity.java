package com.op.crush;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.op.crush.Room.ProgressState;
import com.op.crush.background.FlwService;
import com.op.crush.background.LoadFollower;
import com.op.crush.background.LoadFollowing;
import com.op.crush.models.UserShow;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Switch;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    long loggedUserTwitterId;
    SharedPreferences preferences;
    SharedPreferences preferences1;

    public long nextCursor = -1L;

    TwitterLoginButton loginButton;
    TwitterSession session;

    Switch n;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Twitter.initialize(this);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        preferences1 = getSharedPreferences("Courser", Context.MODE_PRIVATE);


        loginButton = (TwitterLoginButton) findViewById(R.id.login_button);

        if (preferences.getString("log", "").equals("login")) {
            session = TwitterCore.getInstance().getSessionManager().getActiveSession();
//            startService(new Intent(MainActivity.this, FlwService.class));


            loginMethod();

        } else {
            loginButton.setCallback(new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    // Do something with result, which provides a TwitterSession for making API calls
                    session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                    preferences.edit().putString("log", "login").apply();
                    TwitterAuthToken authToken = session.getAuthToken();

                    loggedUserTwitterId = session.getId();
                    //String token = authToken.token;
                    //  String secret = authToken.secret;

                    preferences1.edit().putLong("day",  System.currentTimeMillis()+90000000).apply();

                    MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(session);
                    myTwitterApiClient.getCustomTwitterService().User(session.getUserId(), session.getUserName()).enqueue(new retrofit2.Callback<UserShow>() {
                        @Override
                        public void onResponse(Call<UserShow> call, Response<UserShow> response) {


                            if (response.body() != null) {
                                UserShow show = response.body();
                    /*Toast.makeText(HomeBottomFragment.this, ""+show.getProfile_name() + "\n"
                            +show.getProfile_image_url() + "\n" + show.getFollowers_count(), Toast.LENGTH_SHORT).show();*/
                                int cf = show.getFollowers_count() + show.getFollowings_count();
                                preferences.edit().putInt("CP", cf).apply();

                                loginMethod();
                            }


                        }

                        @Override
                        public void onFailure(Call<UserShow> call, Throwable t) {

                        }
                    });



                }

                @Override
                public void failure(TwitterException exception) {
                    // Do something on failure
                    Toast.makeText(getApplicationContext(), "Login fail " + exception.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

    }


    public void loginMethod() {

        Intent intent = new Intent(MainActivity.this, MainMenu.class);
        startActivity(intent);
        finish();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }


}