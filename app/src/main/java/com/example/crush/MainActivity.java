package com.example.crush;

import androidx.appcompat.app.AppCompatActivity;

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
import android.os.Parcelable;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    long loggedUserTwitterId;
    SharedPreferences preferences;

    TwitterLoginButton loginButton;
    TwitterSession session;
    TwitterAuthClient twitterAuthClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Twitter.initialize(this);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("login", Context.MODE_PRIVATE);


        loginButton = (TwitterLoginButton) findViewById(R.id.login_button);

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

                    loginMethod(session);
                }

                @Override
                public void failure(TwitterException exception) {
                    // Do something on failure
                    Toast.makeText(getApplicationContext(), "Login fail", Toast.LENGTH_LONG).show();
                }
            });


    }

    public void loginMethod(final TwitterSession twitterSession) {

        String userName = twitterSession.getUserName();
        Intent intent = new Intent(MainActivity.this, MainMenu.class);
        intent.putExtra("username", userName);
        startActivity(intent);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }


}