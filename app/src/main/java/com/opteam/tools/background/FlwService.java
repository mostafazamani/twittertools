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
                Builder(LoadFollowing.class, 120, TimeUnit.MINUTES)
                .setConstraints(constraints).build();

        PeriodicWorkRequest workRequest1 = new PeriodicWorkRequest.
                Builder(LoadFollower.class, 120, TimeUnit.MINUTES)
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
                Builder(LoadFollowing.class, 120, TimeUnit.MINUTES)
                .setConstraints(constraints).build();

        PeriodicWorkRequest workRequest1 = new PeriodicWorkRequest.
                Builder(LoadFollower.class, 120, TimeUnit.MINUTES)
                .setConstraints(constraints).build();


        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork("following",
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest);

        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork("follower",
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest1);


    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent intent = new Intent(getApplicationContext(), this.getClass());
        intent.setPackage(getPackageName());
        startService(intent);

        super.onTaskRemoved(rootIntent);
    }


}
