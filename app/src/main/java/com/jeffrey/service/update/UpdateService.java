package com.jeffrey.service.update;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.AndroidException;
import android.util.Log;

import com.jeffrey.activity.R;

import java.io.File;

/**
 * Created by Jun.wang on 2018/10/2.
 */

public class UpdateService extends Service {
    // 服务器国定地址
    private static final String APK_URL_TITLE = "http://10.0.2.2:8080/MyVideoAppServer/Data/MyVideo.apk";

    // 文件存放路径
    private String filePath;

    // 文件下载地址
    private String apkUrl;

    private NotificationManager notificationManager;
    private Notification mNotification;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        filePath = Environment.getExternalStorageDirectory() + "/imooc/myvideo.apk";
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        apkUrl = APK_URL_TITLE;
        notifyUser("开始下载", "开始下载", 0);
        startDownload();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startDownload() {
        UpdateManager.getInstance().startDownload(apkUrl, filePath, new UpdateDownloadListener() {
            @Override
            public void onStarted() {
                Log.e("XXX", "startDownload------onStarted");
            }

            @Override
            public void onPrepared(long contentLength, String downloadUrl) {
                Log.e("XXX", "startDownload------onPrepared");
            }

            @Override
            public void onProgressChanged(int progress, String downloadUrl) {
               // Log.e("XXX", "startDownload------onProgressChanged");
                notifyUser("正在下载", "正在下载", progress);
            }

            @Override
            public void onPaused(int progress, int completeSize, String downloadUrl) {
                Log.e("XXX", "startDownload------onPaused");
                notifyUser("下载失败",
                        "下载失败，请检查网络连接和SD卡存储空间", 0);
                deleteApkFile();
                stopSelf();
            }

            @Override
            public void onFinished(int completeSize, String downloadUrl) {
                Log.e("XXX", "startDownload------onFinished");
                notifyUser("下载完成", "下载完成",100);
                stopSelf(); // 停掉服务自身
                startActivity(getInstallApkIntent());

            }

            @Override
            public void onFailure() {
                Log.e("XXX", "startDownload------onFailure");
                notifyUser("下载失败", "下载失败，请检查网络连接或SD卡存储空间", 0);
                deleteApkFile();
                stopSelf();
            }
        });
    }

    public void notifyUser(String tickerMsg, String message, int progress) {
        notifyThatExceedLv21(tickerMsg, message, progress);
    }

    private void notifyThatExceedLv21(String tickerMsg, String message, int progress) {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this);
        notification.setSmallIcon(R.drawable.bar_code_scan_icon);
        notification.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.arrow_right));
        notification.setContentTitle("ImoocBusiness");
        if (progress > 0 && progress < 100) {
            notification.setProgress(100, progress, false);
        } else {
            // 0,0, false 可以将进度条隐藏
            notification.setProgress(0,0, false);
            notification.setContentText(message);
        }
        notification.setAutoCancel(true);
        notification.setWhen(System.currentTimeMillis());
        notification.setTicker(tickerMsg);
        notification.setContentIntent(progress >= 100 ? getContentIntent() :
                PendingIntent.getActivity(this, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT));
        mNotification = notification.build();
        notificationManager.notify(0, mNotification);
    }

    private PendingIntent getContentIntent() {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, getInstallApkIntent(),
                PendingIntent.FLAG_UPDATE_CURRENT);
        return contentIntent;
    }

    // 下载完成，安装
    private Intent getInstallApkIntent() {
        File apkfile = new File(filePath);
       // Uri apkURI = Uri.fromFile(apkfile);


        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        Uri apkURI = FileProvider.getUriForFile(this,
                getApplicationContext().getPackageName() + ".provider", apkfile);
      //  intent.setDataAndType(Uri.parse("file://" + apkfile.toString()),
       //                         "application/vnd.android.package-archive");
        intent.setDataAndType(apkURI, "application/vnd.android.package-archive");
        return intent;
    }

    // 删除无用apk文件
    private boolean deleteApkFile() {
        File apkFile = new File(filePath);
        if(apkFile.exists() && apkFile.isFile()) {
            return apkFile.delete();
        }
        return false;
    }

}


























