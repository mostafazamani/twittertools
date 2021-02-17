package com.op.crush.downloadvideo;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.op.crush.MyTwitterApiClient;
import com.op.crush.R;
import com.op.crush.MainActivity;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.op.crush.menu.DownloaderBottomFragment.STOPFOREGROUND_ACTION;


public class ClipboardMonitor extends Service {
    IBinder mBinder;
    SharedPreferences.Editor editor;
    SharedPreferences prefs;
    int mStartMode;
    private TwitterSession session;
    ClipboardManager mCM;
    ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener =

            new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public void onPrimaryClipChanged() {
                    String newClip = mCM.getPrimaryClip().getItemAt(0).getText().toString();
                    //   Toast.makeText(getApplicationContext(), newClip, Toast.LENGTH_LONG).show();
                    Log.i("LOGClipboard111111 clip", newClip + "");

//                    TwitterVideoDownloader downloader = new TwitterVideoDownloader(getApplicationContext(), newClip);
//                    downloader.DownloadVideo();


                    MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(session);
                    myTwitterApiClient.getCustomTwitterService().getTwit(gettwitid(newClip)).enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                            if (response.body() != null) {
                                try {

                                    JsonObject jsonObject = (JsonObject) response.body();
                                    JsonObject jsonObject1 = jsonObject.getAsJsonObject("extended_entities");
                                    JsonArray elements = jsonObject1.getAsJsonArray("media");
                                    JsonObject jsonObject2 = (JsonObject) elements.get(0);
                                    String type = jsonObject2.get("type").getAsString();
                                    if (jsonObject2.get("type").getAsString().contains("video")) {
                                        JsonObject jsonObject3 = jsonObject2.getAsJsonObject("video_info");
                                        JsonArray elements1 = jsonObject3.getAsJsonArray("variants");
                                        JsonObject jsonObject4 = (JsonObject) elements1.get(2);
                                        String url = jsonObject4.get("url").getAsString();
                                        downloadurl.DL(url, gettwitid(newClip), ".mp4", getApplicationContext());
                                        Log.i("downloadFileName", "1");
                                    } else if (type.contains("photo")) {
                                        String url = jsonObject2.get("media_url").getAsString();
                                        downloadurl.DL(url, gettwitid(newClip), ".jpg", getApplicationContext());
                                        Log.i("downloadFileName", "1.5");
                                    }

                                    Log.i("downloadFileName", "2");
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), "sorry!! we can't download this", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.i("downloadFileName", "3");
                                Toast.makeText(getApplicationContext(), "check your connection", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "try again", Toast.LENGTH_SHORT).show();

                            Log.i("downloadFileName", "error");
                        }
                    });
                }


            };

    @Override
    public void onCreate() {
        super.onCreate();
        mCM = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mCM.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener);
        session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        startInForeground();
        System.out.println("oncreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            if (intent.getAction() != null) {
                String s = intent.getAction();
                if (s == STOPFOREGROUND_ACTION)
                    stopForegroundService();
            }
        }


        return START_STICKY;
    }

    private void stopForegroundService() {
        Log.i("LOGClipboard111111", "worki 2");
        Log.d("Foreground", "Stop foreground service.");
        // Stop foreground service and remove the notification.

        prefs = getSharedPreferences("PREF_CLIP", MODE_PRIVATE);

        editor = prefs.edit();

        editor.putBoolean("csRunning", false);
        editor.apply();
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();
        if (mCM != null) {
            mCM.removePrimaryClipChangedListener(
                    mOnPrimaryClipChangedListener);
        }
    }

    private void startInForeground() {
        Log.i("LOGClipboard111111", "worki 1");


        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getPackageName() + "-" + getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_baseline_cloud_download_24)
                .setContentTitle("Auto Download")
                .setContentText("copy link to download")
                .setTicker("TICKER")
                // .addAction(R.drawable.ic_download_24dp, getString(R.string.stop_btn), makePendingIntent(STOPFOREGROUND_ACTION))
                .setContentIntent(pendingIntent);
        Notification notification = builder.build();
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(getPackageName() + "--" + "ClipbordManager", getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("copy link to download");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
        prefs = getSharedPreferences("PREF_CLIP", MODE_PRIVATE);
        editor = prefs.edit();
        editor.putBoolean("csRunning", true);
        editor.apply();
        //stopSelf();
        startForeground(1002, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);
        super.onTaskRemoved(rootIntent);
        // this.stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("destroyed", "123123");
        stopForegroundService();

    }

    public PendingIntent makePendingIntent(String name) {
        Intent intent = new Intent(getApplicationContext(), ClipboardMonitor.class);
        intent.setAction(name);
        return PendingIntent.getService(getApplicationContext(), 0, intent, 0);
    }

    public long gettwitid(String t) {

        if (t.contains("https://twitter.com")) {
            if (t.contains("?")) {
                int f = t.lastIndexOf("status");
                int e = t.lastIndexOf("?");
                String g = t.substring(f + 7, e);
                Log.i("downloadFileName1", g);
                long r = Long.parseLong(g);
                return r;
            } else {
                int f = t.lastIndexOf("status");
                String g = t.substring(f + 7);
                Log.i("downloadFileName2", g);
                long r = Long.parseLong(g);
                return r;
            }

        } else {
            return 0;
        }
    }

}

