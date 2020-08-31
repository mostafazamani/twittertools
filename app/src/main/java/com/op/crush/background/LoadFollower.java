package com.op.crush.background;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ProgressUpdater;
import androidx.work.WorkQuery;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.work.impl.WorkDatabase;

import com.google.common.util.concurrent.ListenableFuture;
import com.op.crush.DbFollow;
import com.op.crush.MyTwitterApiClient;
import com.op.crush.models.followmodel;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Response;

public class LoadFollower extends Worker {

    private TwitterSession session;
    private DbFollow db;
    public long nextCursor = -1L;
    private int countFollower = 0;
    SharedPreferences preferences;


    public LoadFollower(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        preferences = context.getSharedPreferences("Courser", Context.MODE_PRIVATE);

    }

    @NonNull
    @Override
    public Result doWork() {
        session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        Log.i("foll", "start2");
        nextCursor = preferences.getLong("FollowerC", -1L);
        countFollower = preferences.getInt("FRC", 0);
        loadFollowers(session, nextCursor);


        return Result.success();
    }

    public void loadFollowers(final TwitterSession twitterSession, long next) {
        db = DbFollow.getInstance(getApplicationContext());
        db.getWritableDatabase();

        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(twitterSession);
        myTwitterApiClient.getCustomTwitterService().FollowersList(twitterSession.getId(), next, 200).enqueue(new retrofit2.Callback() {
            @Override
            public void onResponse(Call call, @NonNull Response response) {
                if (response.body() != null) {
                    final followmodel fol = (followmodel) response.body();
                    if (fol.getResults() != null) {

                        for (int i = 0; i < fol.getResults().size(); i++) {

                            db.AddItem(fol.getResults().get(i), DbFollow.TB_FOLLOWER);

                        }
                    }

                    db.close();

                    preferences.edit().putLong("FollowerC", fol.getNextCursor()).apply();
                    preferences.edit().putInt("FRC", countFollower).apply();

                    if (countFollower == 15) {
                        new CountDownTimer(30000, 1000) {
                            @Override
                            public void onTick(long l) {

                            }

                            @Override
                            public void onFinish() {
                                countFollower = 0;
                                preferences.edit().putInt("FRC", countFollower).apply();
                                if (fol.getNextCursor() != 0) {
                                    loadFollowers(twitterSession, fol.getNextCursor());
                                } else {
                                    preferences.edit().putLong("FollowerC", -1L).apply();
                                }
                            }
                        }.start();
                    } else {

                        countFollower++;

                        Log.i("follwer", String.valueOf(countFollower));
                        if (fol.getNextCursor() != 0) {
                            loadFollowers(twitterSession, fol.getNextCursor());
                        } else {
                            countFollower = 0;
                            preferences.edit().putLong("FollowerC", -1L).apply();
                            preferences.edit().putInt("FIC", countFollower).apply();
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {


            }
        });


    }


}
