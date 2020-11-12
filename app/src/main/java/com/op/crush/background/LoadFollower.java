package com.op.crush.background;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


import com.op.crush.DbFollow;
import com.op.crush.MyTwitterApiClient;
import com.op.crush.Room.ProgressDatabase;
import com.op.crush.Room.ProgressState;
import com.op.crush.models.followmodel;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;


import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Response;

public class LoadFollower extends Worker {

    private final ProgressDatabase database;
    private TwitterSession session;
    private DbFollow db;
    public long nextCursor;
    private int countFollower;
    int pc;
    SharedPreferences preferences;
    private int prog = 0;
    long min;


    public LoadFollower(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        preferences = context.getSharedPreferences("Courser", Context.MODE_PRIVATE);
        pc = 100 / (preferences.getInt("CP", 0) / 200);
        database = ProgressDatabase.getInstance(context);
        nextCursor = preferences.getLong("FollowerC", -1L);
        countFollower = preferences.getInt("FRC", 0);
        min = ((System.currentTimeMillis()) - preferences.getLong("timeRFollower", 16L)) / 60000;
        Log.i("followr", "constructor");
    }

    @NonNull
    @Override
    public Result doWork() {
        session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        Log.i("foll", "start2");

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


                    prog += pc;
                    countFollower++;
                    new progress(database).execute(new ProgressState(prog));
                    preferences.edit().putLong("FollowerC", fol.getNextCursor()).apply();
                    preferences.edit().putInt("FRC", countFollower).apply();
                    preferences.edit().putLong("timeRFollower", System.currentTimeMillis()).apply();
                    if (countFollower == 15 && min < 15) {
                        new CountDownTimer(900000, 1000) {
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
                                    new progress(database).execute(new ProgressState(100));
                                }
                            }
                        }.start();
                    } else {


                        Log.i("follower", String.valueOf(countFollower));
                        if (fol.getNextCursor() != 0) {
                            loadFollowers(twitterSession, fol.getNextCursor());
                        } else {
                            countFollower = 0;
                            preferences.edit().putLong("FollowerC", -1L).apply();
                            preferences.edit().putInt("FRC", countFollower).apply();
                            new progress(database).execute(new ProgressState(100));
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {


            }
        });


    }

    public static class progress extends AsyncTask<ProgressState, Void, Void> {

        ProgressDatabase database;

        public progress(ProgressDatabase database) {
            this.database = database;
        }

        @Override
        protected Void doInBackground(ProgressState... progressStates) {
            database.progressDao().insert(progressStates[0]);
            return null;
        }
    }

}
