package com.op.crush;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.op.crush.Room.CircleCrush.UserCrush;
import com.op.crush.Room.ProgressState;
import com.op.crush.background.FlwService;
import com.op.crush.background.LoadFollower;
import com.op.crush.background.LoadFollowing;
import com.op.crush.downloadvideo.downloadurl;
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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
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
    private SharedPreferences preferences2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Twitter.initialize(this);
        setContentView(R.layout.activity_main);

        if (getAppIntro(this)) {
            Intent i = new Intent(MainActivity.this, IntroActivityApp.class);
            startActivity(i);
        }

        preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        preferences1 = getSharedPreferences("Courser", Context.MODE_PRIVATE);
        preferences2 = getSharedPreferences("AdL", Context.MODE_PRIVATE);
        String url = "https://www.dropbox.com/s/17b9g70xy9t8gpl/ad.txt?dl=1";
        final RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                preferences2.edit().putString("ad", response.trim()).apply();
                Log.i("adl", response);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("adl", error.getMessage());
            }
        });

        queue.add(stringRequest);


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

                    preferences1.edit().putLong("dayfollowing", System.currentTimeMillis() - 90000000).apply();
                    preferences1.edit().putLong("dayfollower", System.currentTimeMillis() - 90000000).apply();

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
                            Toast.makeText(MainActivity.this, "Check your connection", Toast.LENGTH_SHORT).show();
                        }
                    });


                }

                @Override
                public void failure(TwitterException exception) {
                    // Do something on failure
                    Toast.makeText(getApplicationContext(), "Login fail , try again ", Toast.LENGTH_LONG).show();
                }
            });
        }

    }


    public void loginMethod() {

        Intent intent = new Intent(MainActivity.this, MainMenu.class);
        startActivity(intent);
        finish();

    }
    private boolean getAppIntro(Activity mainActivity) {
        SharedPreferences preferences;
        preferences = mainActivity.getSharedPreferences("intro", Context.MODE_PRIVATE);
        return preferences.getBoolean("AppIntro", true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode, data);

    }


}