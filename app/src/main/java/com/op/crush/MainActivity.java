package com.op.crush;

import androidx.appcompat.app.AppCompatActivity;

import com.op.crush.background.FlwService;
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
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    long loggedUserTwitterId;
    SharedPreferences preferences;

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
                    startService(new Intent(MainActivity.this, FlwService.class));

                    loginMethod();

                }

                @Override
                public void failure(TwitterException exception) {
                    // Do something on failure
                    Toast.makeText(getApplicationContext(), "Login fail "+ exception.getMessage(), Toast.LENGTH_LONG).show();
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