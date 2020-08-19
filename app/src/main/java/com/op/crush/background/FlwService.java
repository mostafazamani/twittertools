package com.op.crush.background;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.op.crush.DbFollowers;
import com.op.crush.DbFollowings;
import com.op.crush.MainMenu;
import com.op.crush.MyTwitterApiClient;
import com.op.crush.models.followmodel;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FlwService extends Service {
    private TwitterSession session;
    private DbFollowers dbHelper;
    private DbFollowings db;
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
        onTaskRemoved(intent);
        session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        dbHelper = new DbFollowers(FlwService.this);
//        db = new DbFollowings(FlwService.this);
        db = DbFollowings.getInstance(FlwService.this);
        //      loadFollowers(session,nextCursor);
        db.getWritableDatabase();
        db.getReadableDatabase();

        loadFollowings(session, nextCursor);

        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent intent = new Intent(getApplicationContext(), this.getClass());
        intent.setPackage(getPackageName());
        startService(intent);

        super.onTaskRemoved(rootIntent);
    }

    public void loadFollowers(final TwitterSession twitterSession, long next) {
        dbHelper = new DbFollowers(getApplicationContext());
        dbHelper.getWritableDatabase();
        dbHelper.getReadableDatabase();
        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(twitterSession);
        myTwitterApiClient.getCustomTwitterService().FollowersList(twitterSession.getId(), next, 200).enqueue(new retrofit2.Callback() {
            @Override
            public void onResponse(Call call, @NonNull Response response) {
                if (response.body() != null) {
                    followmodel fol = (followmodel) response.body();
                    if (fol.getResults() != null)
                        for (int i = 0; i < fol.getResults().size(); i++) {

                            dbHelper.AddItem(fol.getResults().get(i));
                        }
                    dbHelper.close();

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

try {


    MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(twitterSession);
    myTwitterApiClient.getCustomTwitterService().FollowingList(twitterSession.getId(), next, 200).enqueue(new retrofit2.Callback() {
        @Override
        public void onResponse(Call call, @NonNull Response response) {
            if (response.body() != null) {
                followmodel fol = (followmodel) response.body();
                if (fol.getResults() != null)
                    for (int i = 0; i < fol.getResults().size(); i++) {

                        db.AddItem(fol.getResults().get(i));
                    }


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


        }
    });
}catch (OutOfMemoryError e){
    Log.i("error",e.getMessage());
}

    }

}
