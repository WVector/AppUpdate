package com.vector.update_app.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;

import com.vector.update_app.HttpManager;
import com.vector.update_app.R;
import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.utils.Utils;

import java.io.File;


/**
 * 后台下载
 */
public class DownloadService extends Service {

    private static final int NOTIFY_ID = 0;
    private static final String TAG = DownloadService.class.getSimpleName();
    public static boolean isRunning = false;
    private NotificationManager mNotificationManager;
    private DownloadBinder binder = new DownloadBinder();
    private NotificationCompat.Builder mBuilder;
//    /**
//     * 开启服务方法
//     *
//     * @param context
//     */
//    public static void startService(Context context) {
//        Intent intent = new Intent(context, DownloadService.class);
//        context.startService(intent);
//    }

    public static void bindService(Context context, ServiceConnection connection) {
        Intent intent = new Intent(context, DownloadService.class);
        context.startService(intent);
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        isRunning = true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // 返回自定义的DownloadBinder实例
        return binder;
    }

    @Override
    public void onDestroy() {
        mNotificationManager = null;
        super.onDestroy();
    }

    /**
     * 创建通知
     */
    private void setUpNotification() {
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("开始下载")
                .setContentText("正在连接服务器")
                .setSmallIcon(R.mipmap.lib_update_app_update_icon)
                .setLargeIcon(Utils.drawableToBitmap(Utils.getAppIcon(DownloadService.this)))
                .setOngoing(true)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis());
        mNotificationManager.notify(NOTIFY_ID, mBuilder.build());
    }

    /**
     * 下载模块
     */
    private void startDownload(UpdateAppBean updateApp, final DownloadCallback callback) {
        String apkUrl = updateApp.getApkFileUrl();
        if (TextUtils.isEmpty(apkUrl)) {
            String contentText = "新版本下载路径错误";
            stop(contentText);
            return;
        }
        final String appName = apkUrl.substring(apkUrl.lastIndexOf("/") + 1, apkUrl.length());

        if (!appName.endsWith(".apk")) {
            String contentText = "下载包有错误";
            stop(contentText);
            return;
        }
        File appDir = new File(updateApp.getTargetPath());
        if (!appDir.exists()) {
            appDir.mkdirs();
        }

        String target = appDir + File.separator + updateApp.getNewVersion();

        updateApp.getHttpManager().download(apkUrl, target, appName, new FileDownloadCallBack(callback));
    }

    private void stop(String contentText) {
        mBuilder.setContentTitle(Utils.getAppName(DownloadService.this)).setContentText(contentText);
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(NOTIFY_ID, notification);
        close();
    }

    private void close() {
        stopSelf();
        isRunning = false;
    }

    /**
     * 进度条回调接口
     */
    public interface DownloadCallback {
        void onStart();

        void onProgress(float progress);

        void setMax(float total);

        void onFinish();

        void onError(String msg);
    }

    /**
     * DownloadBinder中定义了一些实用的方法
     *
     * @author user
     */
    public class DownloadBinder extends Binder {
        /**
         * 开始下载
         *
         * @param updateApp 新app信息
         * @param callback  下载回调
         */
        public void start(UpdateAppBean updateApp, DownloadCallback callback) {
            //初始化通知栏
            setUpNotification();
            //下载
            startDownload(updateApp, callback);
        }
    }

    class FileDownloadCallBack implements HttpManager.FileCallback {
        private final DownloadCallback mCallBack;

        public FileDownloadCallBack(DownloadCallback callback) {
            super();
            this.mCallBack = callback;
        }

        @Override
        public void onBefore() {
            if (mCallBack != null) {
                mCallBack.onStart();
            }
        }

        @Override
        public void onProgress(float progress, long total) {
            int rate = Math.round(progress * 100);
            if (mCallBack != null) {
                mCallBack.setMax(total);
                mCallBack.onProgress(progress * total);
            }
            mBuilder.setContentTitle("正在下载：" + Utils.getAppName(DownloadService.this))
                    .setContentText(rate + "%")
                    .setProgress(100, rate, false)
                    .setWhen(System.currentTimeMillis());
            Notification notification = mBuilder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            mNotificationManager.notify(NOTIFY_ID, notification);
        }

        @Override
        public void onError(String error) {
            Toast.makeText(DownloadService.this, "更新新版本出错，" + error, Toast.LENGTH_SHORT).show();
            //App前台运行
            if (mCallBack != null) {
                mCallBack.onError(error);
            }
            try {
                mNotificationManager.cancel(NOTIFY_ID);
                close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        @Override
        public void onResponse(File file) {
            if (mCallBack != null) {
                mCallBack.onFinish();
            }

            Uri fileUri = FileProvider.getUriForFile(DownloadService.this, getApplicationContext().getPackageName() + ".fileProvider", file);
            if (Utils.isAppOnForeground(DownloadService.this)) {
                //App前台运行
                mNotificationManager.cancel(NOTIFY_ID);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
                } else {
                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                }
                if (getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
                    startActivity(intent);
                }
            } else {
                //App后台运行
                Intent intent = new Intent(Intent.ACTION_VIEW);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
                } else {
                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                }
                //更新参数,注意flags要使用FLAG_UPDATE_CURRENT
                PendingIntent contentIntent = PendingIntent.getActivity(DownloadService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(contentIntent)
                        .setContentTitle(Utils.getAppName(DownloadService.this))
                        .setContentText("下载完成，请点击安装")
                        .setProgress(0, 0, false)
//                        .setAutoCancel(true)
                        .setDefaults((Notification.DEFAULT_ALL));
                Notification notification = mBuilder.build();
                notification.flags = Notification.FLAG_AUTO_CANCEL;
                mNotificationManager.notify(NOTIFY_ID, notification);
            }
            //下载完自杀
            close();
        }
    }
}
