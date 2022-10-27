package com.opteam.tools;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.opteam.tools.models.UserShow;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import retrofit2.Call;
import retrofit2.Response;

public class Launcher extends AppCompatActivity {

    private SharedPreferences preferences;
    private SharedPreferences preferences1;
    private SharedPreferences preferences2;
    TwitterSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Twitter.initialize(this);
        setContentView(R.layout.launcher_activity);

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
                Log.i("adl", "error");
            }
        });

        queue.add(stringRequest);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Launch();

            }
        },1000);


//        Thread myThread = new Thread()
//        {
//            @Override
//            public void run() {
//                try {
//                    sleep(800);
//                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
//
//
//                    startActivity(intent);
//                    //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                    finish();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        myThread.start();
    }

    public void Launch(){

        if (preferences.getString("log", "").equals("login")) {
            session = TwitterCore.getInstance().getSessionManager().getActiveSession();
//            startService(new Intent(MainActivity.this, FlwService.class));
            MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(session);
            myTwitterApiClient.getCustomTwitterService().User(session.getUserId(), session.getUserName()).enqueue(new retrofit2.Callback<UserShow>() {
                @Override
                public void onResponse(Call<UserShow> call, Response<UserShow> response) {


                    if (response.body() != null) {
                        UserShow show = response.body();
                    /*Toast.makeText(HomeBottomFragment.this, ""+show.getProfile_name() + "\n"
                            +show.getProfile_image_url() + "\n" + show.getFollowers_count(), Toast.LENGTH_SHORT).show();*/
                        int cf = show.getFollowings_count();
                        int cff = show.getFollowers_count();
                        preferences1.edit().putInt("CP", cf).apply();
                        preferences1.edit().putInt("CPf", cff).apply();
                        Log.i("foll",String.valueOf(cff));

                    }

                    Intent intent = new Intent(Launcher.this, MainMenu.class);
                    startActivity(intent);
                    finish();

                }

                @Override
                public void onFailure(Call<UserShow> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Check your connection", Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(Launcher.this);
                    builder.setMessage("Check your connection").setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Launch();

                        }
                    })
                            .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }).show();


                }
            });


        }else {

            Intent intent = new Intent(Launcher.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
