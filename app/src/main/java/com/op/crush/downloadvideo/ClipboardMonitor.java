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

import androidx.core.app.NotificationCompat;
import com.op.crush.R;
import com.op.crush.MainActivity;


public class ClipboardMonitor extends Service {
    IBinder mBinder;
    SharedPreferences.Editor editor;
    SharedPreferences prefs;
    int mStartMode;
    ClipboardManager mCM;
    ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener =

            new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public void onPrimaryClipChanged() {
                    String newClip = mCM.getPrimaryClip().getItemAt(0).getText().toString();
                    //   Toast.makeText(getApplicationContext(), newClip, Toast.LENGTH_LONG).show();
                    Log.i("LOGClipboard111111 clip", newClip + "");

//                    WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                            WindowManager.LayoutParams.WRAP_CONTENT,
//                            WindowManager.LayoutParams.WRAP_CONTENT,
//                            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
//                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
//                                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                            PixelFormat.TRANSLUCENT);
//
//                    params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
//                    params.x = 0;
//                    params.y = 100;


                    TwitterVideoDownloader downloader = new TwitterVideoDownloader(getApplicationContext(), newClip);
                    downloader.DownloadVideo();


                }
            };

    @Override
    public void onCreate() {
        super.onCreate();
        mCM = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mCM.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            String action = intent.getAction();

            switch (action) {
                case  "startforeground":
                    startInForeground();
                    break;
                case "stopforeground":
                    stopForegroundService();
                    break;
            }
        } catch (Exception e) {
            System.out.println("Null pointer exception");
        }


        return START_STICKY;
    }

    private void stopForegroundService() {
        Log.i("LOGClipboard111111", "worki 2");
        Log.d("Foreground", "Stop foreground service.");
        // Stop foreground service and remove the notification.
        stopForeground(true);
        // Stop the foreground service.
        stopSelf();
        mCM.removePrimaryClipChangedListener(mOnPrimaryClipChangedListener);
    }

    private void startInForeground() {
        Log.i("LOGClipboard111111", "worki 1");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getPackageName() + "-" + getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_baseline_cloud_download_24)
                .setContentTitle("Auto Download")
                .setContentText("Copy the link of video to start download")
                .setTicker("TICKER")
                .addAction(R.drawable.ic_baseline_cloud_download_24, "Stop", makePendingIntent("stopforeground"))
                .setContentIntent(pendingIntent);
        Notification notification = builder.build();
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(getPackageName() + "--" + "ClipbordManager", getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Auto Download");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
        mCM.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener);
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
        stopForeground(true);
        stopSelf();
        if (mCM != null) {
            mCM.removePrimaryClipChangedListener(
                    mOnPrimaryClipChangedListener);
        }

//        if(prefs.getBoolean("csRunning",false)) {
//                Intent broadcastIntent = new Intent();
//                broadcastIntent.setAction("restartservice");
//                broadcastIntent.setClass(this, Restarter.class);
//                this.sendBroadcast(broadcastIntent);
//            }
    }

    public PendingIntent makePendingIntent(String name) {
        Intent intent = new Intent(getApplicationContext(), ClipboardMonitor.class);
        intent.setAction(name);
        return PendingIntent.getService(getApplicationContext(), 0, intent, 0);
    }

}

