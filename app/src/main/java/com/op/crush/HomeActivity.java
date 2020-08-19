package com.op.crush;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.op.crush.adapter.ListAdapter;
import com.op.crush.models.followmodel;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;


import retrofit2.Call;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private HomeActivity activity = this;
    public ListView mainListView;
    public ListAdapter adapter;
    DbFollowers dbHelper;
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



//        loginMethod(session, nextCursor);




    }


    public void loginMethod(final TwitterSession twitterSession, long next) {
        dbHelper = new DbFollowers(activity);
        dbHelper.getWritableDatabase();
        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(twitterSession);
        myTwitterApiClient.getCustomTwitterService().FollowersList(twitterSession.getId(), next, 200).enqueue(new retrofit2.Callback() {
            @Override
            public void onResponse(Call call, @NonNull Response response) {
                if (response.body() != null) {
                    followmodel fol = (followmodel) response.body();
                    if (fol.getResults() != null)
                    for (int i = 0 ; i < fol.getResults().size() ; i++){

                        dbHelper.AddItem(fol.getResults().get(i));
                    }
                    dbHelper.close();



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