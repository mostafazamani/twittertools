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


public class LoadFollowing extends Worker {

    private final int pc;
    private TwitterSession session;
    private DbFollow db;
    public long nextCursor;
    public long day;
    private int countFollowing;
    private int prog = 0;
    SharedPreferences preferences;
    ProgressDatabase database;
    long min;
    long ou;

    public LoadFollowing(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        preferences = context.getSharedPreferences("Courser", Context.MODE_PRIVATE);
        pc = 100 / (preferences.getInt("CP", -1) / 200);
        database = ProgressDatabase.getInstance(context);
        nextCursor = preferences.getLong("FollowingC", -1L);
        countFollowing = preferences.getInt("FIC", 0);
        day = preferences.getLong("day", 0);
        min = ((System.currentTimeMillis()) - preferences.getLong("timeRFollowing", System.currentTimeMillis())) / 60000;
        ou = ((System.currentTimeMillis()) - preferences.getLong("day", System.currentTimeMillis())) / 60000;
        Log.i("following", "constructor" + pc);
    }

    @NonNull
    @Override
    public Result doWork() {
        session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        Log.i("foll", "start1");

        lFollowings(session, nextCursor);

        return Result.success();
    }

    public void lFollowings(final TwitterSession twitterSession, long next) {
        db = DbFollow.getInstance(getApplicationContext());
        db.getWritableDatabase();
        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(twitterSession);
        myTwitterApiClient.getCustomTwitterService().FollowingList(twitterSession.getId(), next, 200).enqueue(new retrofit2.Callback() {
            @Override
            public void onResponse(Call call, @NonNull Response response) {
                if (response.body() != null) {

                    final followmodel fol = (followmodel) response.body();
                    if (fol.getResults() != null) {

                        for (int i = 0; i < fol.getResults().size(); i++) {
                            if (fol.getResults().get(i) != null)
                                db.AddItem(fol.getResults().get(i), DbFollow.TB_FOLLOWING);
                        }
                    }

                    db.close();


                    prog += pc;
                    countFollowing++;
                    new progress(database).execute(new ProgressState(prog));
                    preferences.edit().putLong("FollowingC", fol.getNextCursor()).apply();
                    preferences.edit().putInt("FIC", countFollowing).apply();
                    preferences.edit().putLong("timeRFollowing", System.currentTimeMillis()).apply();

                    if (countFollowing == 15 && min < 15) {
                        new CountDownTimer(900000, 1000) {
                            @Override
                            public void onTick(long l) {

                            }

                            @Override
                            public void onFinish() {
                                countFollowing = 0;
                                preferences.edit().putInt("FIC", countFollowing).apply();
                                if (fol.getNextCursor() != 0) {
                                    lFollowings(twitterSession, fol.getNextCursor());
                                } else {
                                    preferences.edit().putLong("FollowingC", -1L).apply();
                                    new progress(database).execute(new ProgressState(100));
                                }
                            }
                        }.start();
                    } else {


                        Log.i("following", String.valueOf(countFollowing));

                        if (fol.getNextCursor() != 0) {
                            lFollowings(twitterSession, fol.getNextCursor());
                        } else {
                            countFollowing = 0;
                            preferences.edit().putLong("FollowingC", -1L).apply();
                            preferences.edit().putInt("FIC", countFollowing).apply();
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
