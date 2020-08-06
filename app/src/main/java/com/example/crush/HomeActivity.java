package com.example.crush;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.example.crush.adapter.ListAdapter;
import com.example.crush.models.followingmodel;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;


import retrofit2.Call;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private HomeActivity activity = this;
    public ListView mainListView;
    public ListAdapter adapter;

    public long nextCursor = -1L;

    public TwitterSession session;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Twitter.initialize(this);
        setContentView(R.layout.activity_home);

        session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        Toast.makeText(activity, "" + session.getUserName() + "    \n" + session.getUserId(), Toast.LENGTH_SHORT).show();
        mainListView = (ListView) findViewById(R.id.mainListView);

        adapter = new ListAdapter(getApplicationContext());
        mainListView.setAdapter(adapter);


        loginMethod(session, nextCursor);


    }


    public void loginMethod(final TwitterSession twitterSession, long next) {


        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(twitterSession);
        myTwitterApiClient.getCustomTwitterService().FollowersList(twitterSession.getId(), next, 200).enqueue(new retrofit2.Callback() {
            @Override
            public void onResponse(Call call, @NonNull Response response) {
                if (response.body() != null) {
                    followingmodel fol = (followingmodel) response.body();


                    adapter.AddItemToList(fol.getResults());
                    adapter.notifyDataSetChanged();

                    Toast.makeText(HomeActivity.this, "" + fol.getNextCursor(), Toast.LENGTH_SHORT).show();

                    if (fol.getNextCursor() != 0) loginMethod(twitterSession, fol.getNextCursor());
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {

                Toast.makeText(HomeActivity.this, "wtf", Toast.LENGTH_SHORT).show();

            }
        });


        Toast.makeText(activity, "end", Toast.LENGTH_SHORT).show();
    }


}