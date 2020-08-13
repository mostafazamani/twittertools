package com.example.crush;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crush.models.followingmodel;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    long loggedUserTwitterId;
    SharedPreferences preferences;

    public long nextCursor = -1L;

    TwitterLoginButton loginButton;
    TwitterSession session;
    TwitterAuthClient twitterAuthClient;
    private DbFollowers dbHelper;

    Switch n;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Twitter.initialize(this);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("login", Context.MODE_PRIVATE);


        loginButton = (TwitterLoginButton) findViewById(R.id.login_button);

        if (preferences.getString("log", "").equals("login")) {
            session = TwitterCore.getInstance().getSessionManager().getActiveSession();
            load(session, nextCursor);

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


                    load(session, nextCursor);


                }

                @Override
                public void failure(TwitterException exception) {
                    // Do something on failure
                    Toast.makeText(getApplicationContext(), "Login fail", Toast.LENGTH_LONG).show();
                }
            });
        }

    }


    public void loginMethod() {

        Intent intent = new Intent(MainActivity.this, MainMenu.class);
        startActivity(intent);


    }


    public void load(final TwitterSession twitterSession, long next) {
        dbHelper = new DbFollowers(MainActivity.this);
        dbHelper.getWritableDatabase();
        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(twitterSession);
        myTwitterApiClient.getCustomTwitterService().FollowersList(twitterSession.getId(), next, 200).enqueue(new retrofit2.Callback() {
            @Override
            public void onResponse(Call call, @NonNull Response response) {
                if (response.body() != null) {
                    followingmodel fol = (followingmodel) response.body();
                    if (fol.getResults() != null)
                        for (int i = 0; i < fol.getResults().size(); i++) {

                            dbHelper.AddItem(fol.getResults().get(i));
                        }
                    dbHelper.close();

                    Toast.makeText(MainActivity.this, ""+fol.getNextCursor(), Toast.LENGTH_SHORT).show();
                    if (fol.getNextCursor() != 0) {
                        load(twitterSession, fol.getNextCursor());
                    } else {
                        Intent intent = new Intent(MainActivity.this, MainMenu.class);
                        startActivity(intent);
                    }

                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {


            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }


}