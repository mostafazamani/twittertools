package com.opteam.tools.background;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.opteam.tools.DbFollow;
import com.opteam.tools.MyTwitterApiClient;
import com.opteam.tools.models.followmodel;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class FlwService extends Service {
    private TwitterSession session;
    private DbFollow db;
    public long nextCursor = -1L;
    private int countFollower = 0;
    private int countFollowing = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i("onStartCommand", "c");

        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(true)
                .build();

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.
                Builder(LoadFollowing.class, 2, TimeUnit.MINUTES)
                .setConstraints(constraints).build();

        PeriodicWorkRequest workRequest1 = new PeriodicWorkRequest.
                Builder(LoadFollower.class, 2, TimeUnit.MINUTES)
                .setConstraints(constraints).build();


        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork("following",
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest);

        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork("follower",
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest1);

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.i("create", "c");
        Twitter.initialize(this);
        session = TwitterCore.getInstance().getSessionManager().getActiveSession();
//
//        loadFollowers(session, nextCursor);
//        loadFollowings(session, nextCursor);
        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(true)
                .build();

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.
                Builder(LoadFollowing.class, 720, TimeUnit.MINUTES)
                .setConstraints(constraints).build();

        PeriodicWorkRequest workRequest1 = new PeriodicWorkRequest.
                Builder(LoadFollower.class, 720, TimeUnit.MINUTES)
                .setConstraints(constraints).build();


        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork("following",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest);

        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork("follower",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest1);


    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent intent = new Intent(getApplicationContext(), this.getClass());
        intent.setPackage(getPackageName());
        startService(intent);

        super.onTaskRemoved(rootIntent);
    }

    public void loadFollowers(final TwitterSession twitterSession, long next) {
        db = DbFollow.getInstance(this);
        db.getWritableDatabase();

        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(twitterSession);
        myTwitterApiClient.getCustomTwitterService().FollowersList(twitterSession.getId(), next, 200).enqueue(new retrofit2.Callback() {
            @Override
            public void onResponse(Call call, @NonNull Response response) {
                if (response.body() != null) {
                    followmodel fol = (followmodel) response.body();
                    if (fol.getResults() != null) {

                        for (int i = 0; i < fol.getResults().size(); i++) {

                            db.AddItem(fol.getResults().get(i), DbFollow.TB_FOLLOWER);

                        }
                    }

                    db.close();
                    countFollower++;
                    Log.i("follwer", String.valueOf(countFollower));
                    if (fol.getNextCursor() != 0) {
                        loadFollowers(twitterSession, fol.getNextCursor());
                    } else {

                    }

                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {


            }
        });


    }

    public void loadFollowings(final TwitterSession twitterSession, long next) {
        db = DbFollow.getInstance(this);
        db.getWritableDatabase();
        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(twitterSession);
        myTwitterApiClient.getCustomTwitterService().FollowingList(twitterSession.getId(), next, 200).enqueue(new retrofit2.Callback() {
            @Override
            public void onResponse(Call call, @NonNull Response response) {
                if (response.body() != null) {

                    followmodel fol = (followmodel) response.body();
                    if (fol.getResults() != null) {

                        for (int i = 0; i < fol.getResults().size(); i++) {
                            if (fol.getResults().get(i) != null)
                                db.AddItem(fol.getResults().get(i), DbFollow.TB_FOLLOWING);
                        }
                    }

                    db.close();
                    countFollowing++;
                    Log.i("following", String.valueOf(countFollowing));

                    if (fol.getNextCursor() != 0) {
                        loadFollowings(twitterSession, fol.getNextCursor());
                    } else {

                    }

                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.i("following", String.valueOf(countFollowing));

            }
        });


    }

}
