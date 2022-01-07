package com.opteam.tools.background;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


import com.opteam.tools.DbFollow;
import com.opteam.tools.MyTwitterApiClient;
import com.opteam.tools.Room.ProgressDatabase;
import com.opteam.tools.Room.ProgressState;
import com.opteam.tools.models.followmodel;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;


import retrofit2.Call;
import retrofit2.Response;

public class LoadFollower extends Worker {

    private final ProgressDatabase database;
    private TwitterSession session;
    private DbFollow db;
    public long nextCursor;
    public long day;
    private int countFollower;
    int pc;
    SharedPreferences preferences;
    private int prog = 0;
    long min;
    long ou;


    public LoadFollower(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        Twitter.initialize(context);
        preferences = context.getSharedPreferences("Courser", Context.MODE_PRIVATE);
        pc = 100 / (preferences.getInt("CP", -1) / 200);
        database = ProgressDatabase.getInstance(context);
        nextCursor = preferences.getLong("FollowerC", -1L);
        countFollower = preferences.getInt("FRC", 0);
        day = preferences.getLong("day", 0);
        min = ((System.currentTimeMillis()) - preferences.getLong("timeRFollower", 16L)) / 60000;
        ou = ((System.currentTimeMillis()) - preferences.getLong("dayfollower", System.currentTimeMillis())) / 60000;
        Log.i("followr", "constructor");
        db = DbFollow.getInstance(getApplicationContext());
        db.getWritableDatabase();

    }

    @NonNull
    @Override
    public Result doWork() {
        session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        Log.i("foll", "start2");
        if (ou > 720) {
            if (nextCursor == -1) {
                preferences.edit().putInt("FollowerCount", 0).apply();
                db.DropTable(DbFollow.TB_FOLLOWER);
                db.close();
            }
            loadFollowers(session, nextCursor);
        } else if (nextCursor != -1)
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
                                    preferences.edit().putInt("FollowerCount", 1).apply();
                                    new progress(database).execute(new ProgressState(100));
                                    preferences.edit().putLong("dayfollower", System.currentTimeMillis()).apply();
                                    db.close();
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
                            preferences.edit().putInt("FollowerCount", 1).apply();
                            preferences.edit().putInt("FRC", countFollower).apply();
                            new progress(database).execute(new ProgressState(100));
                            preferences.edit().putLong("dayfollower", System.currentTimeMillis()).apply();
                            db.close();
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
