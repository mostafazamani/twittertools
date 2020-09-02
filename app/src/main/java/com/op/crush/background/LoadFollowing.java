package com.op.crush.background;

import android.app.Activity;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.Data;
import androidx.work.ProgressUpdater;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;
import com.op.crush.DbFollow;
import com.op.crush.MainMenu;
import com.op.crush.MyTwitterApiClient;
import com.op.crush.Room.ProgreesRepository;
import com.op.crush.Room.ProgressDatabase;
import com.op.crush.Room.ProgressState;
import com.op.crush.Room.ProgressViewModel;
import com.op.crush.models.followmodel;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.UUID;
import java.util.logging.Handler;

import retrofit2.Call;
import retrofit2.Response;

import static java.util.jar.Pack200.Packer.PROGRESS;


public class LoadFollowing extends Worker {

    private final int pc;
    private TwitterSession session;
    private DbFollow db;
    public long nextCursor = -1L;
    private int countFollowing = 0;
    SharedPreferences preferences;
    private int prog = 0;
    private Context context;
    ProgressDatabase database;


    public LoadFollowing(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        preferences = context.getSharedPreferences("Courser", Context.MODE_PRIVATE);
        pc = 100 / (preferences.getInt("CP", 0) / 200);
        database = ProgressDatabase.getInstance(context);

    }

    @NonNull
    @Override
    public Result doWork() {
        session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        Log.i("foll", "start1");
        nextCursor = preferences.getLong("FollowingC", -1L);
        countFollowing = preferences.getInt("FIC", 0);
        new CountDownTimer(2000, 1000) {

            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                loadFollowings(session, nextCursor);
            }
        }.start();

        return Result.success();
    }

    public void loadFollowings(final TwitterSession twitterSession, long next) {
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

                    if (countFollowing == 15) {
                        new CountDownTimer(900000, 1000) {
                            @Override
                            public void onTick(long l) {

                            }

                            @Override
                            public void onFinish() {
                                countFollowing = 0;
                                preferences.edit().putInt("FIC", countFollowing).apply();
                                if (fol.getNextCursor() != 0) {
                                    loadFollowings(twitterSession, fol.getNextCursor());
                                } else {
                                    preferences.edit().putLong("FollowingC", -1L).apply();
                                    new progress(database).execute(new ProgressState(100));
                                }
                            }
                        }.start();
                    } else {


                        Log.i("following", String.valueOf(countFollowing));

                        if (fol.getNextCursor() != 0) {
                            loadFollowings(twitterSession, fol.getNextCursor());
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
