package com.op.crush.downloadvideo;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.op.crush.R;

import java.io.File;


public class downloadurl {
    private static String DOWNLOAD_DIRECTORY = "crush_downloader";
    private static String DOWNLOAD_NAME = "crush_downloader_";
    public static DownloadManager downloadManager;
    private static long downloadID;


    public static void DL(String url, long id, String ext, Context context) {

        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle("Download");
        request.setDescription("Downloading...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        String folderName = DOWNLOAD_DIRECTORY;
        String mBaseFolderPath = Environment.getExternalStorageDirectory() + File.separator + folderName;

        File mBaseFolderPathfile = new File(mBaseFolderPath);

        if (!mBaseFolderPathfile.exists()) {
            mBaseFolderPathfile.mkdir();
        }
        String[] bits = mBaseFolderPath.split("/");
        String Dir = bits[bits.length - 1];
        //  request.setDestinationUri(new File(mBaseFolderPath).);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, DOWNLOAD_NAME + id + ext);

        } else {
            request.setDestinationInExternalPublicDir(Dir, DOWNLOAD_NAME + id + ext);

        }

        request.allowScanningByMediaScanner();
        downloadID = downloadManager.enqueue(request);
        Log.e("downloadFileName", DOWNLOAD_NAME + id + ext);

        if (downloadID != 0){
            iUtils.ShowToast(context, "download starting...");
        }else {
            iUtils.ShowToast(context, "something was wrong!");
        }
        Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                iUtils.ShowToast(context, context.getResources().getString(R.string.don_start));

            }
        };

    }

}
