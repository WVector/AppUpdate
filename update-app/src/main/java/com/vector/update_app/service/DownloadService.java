package com.vector.update_app.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.vector.update_app.HttpManager;
import com.vector.update_app.R;
import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.utils.AppUpdateUtils;

import java.io.File;


/**
 * 后台下载
 */
public class DownloadService extends Service {

    private static final int NOTIFY_ID = 0;
    private static final String TAG = DownloadService.class.getSimpleName();
    private static final String CHANNEL_ID = "app_update_id";
    private static final CharSequence CHANNEL_NAME = "app_update_channel";

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
    private boolean mDismissNotificationProgress = false;

    public static void bindService(Context context, ServiceConnection connection) {
        Intent intent = new Intent(context, DownloadService.class);
        context.startService(intent);
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        isRunning = true;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        isRunning = false;
        return super.onUnbind(intent);
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
        if (mDismissNotificationProgress) {
            return;
        }


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            //设置绕过免打扰模式
//            channel.setBypassDnd(false);
//            //检测是否绕过免打扰模式
//            channel.canBypassDnd();
//            //设置在锁屏界面上显示这条通知
//            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
//            channel.setLightColor(Color.GREEN);
//            channel.setShowBadge(true);
//            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            channel.enableVibration(false);
            channel.enableLights(false);

            mNotificationManager.createNotificationChannel(channel);
        }


        mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        mBuilder.setContentTitle("开始下载")
                .setContentText("正在连接服务器")
                .setSmallIcon(R.mipmap.lib_update_app_update_icon)
                .setLargeIcon(AppUpdateUtils.drawableToBitmap(AppUpdateUtils.getAppIcon(DownloadService.this)))
                .setOngoing(true)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis());
        mNotificationManager.notify(NOTIFY_ID, mBuilder.build());
    }

    /**
     * 下载模块
     */
    private void startDownload(UpdateAppBean updateApp, final DownloadCallback callback) {

        mDismissNotificationProgress = updateApp.isDismissNotificationProgress();

        String apkUrl = updateApp.getApkFileUrl();
        if (TextUtils.isEmpty(apkUrl)) {
            String contentText = "新版本下载路径错误";
            stop(contentText);
            return;
        }
        String appName = AppUpdateUtils.getApkName(updateApp);

        File appDir = new File(updateApp.getTargetPath());
        if (!appDir.exists()) {
            appDir.mkdirs();
        }

        String target = appDir + File.separator + updateApp.getNewVersion();

        updateApp.getHttpManager().download(apkUrl, target, appName, new FileDownloadCallBack(callback));
    }

    private void stop(String contentText) {
        if (mBuilder != null) {
            mBuilder.setContentTitle(AppUpdateUtils.getAppName(DownloadService.this))
                    .setContentText(contentText);
            Notification notification = mBuilder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            mNotificationManager.notify(NOTIFY_ID, notification);
        }
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
        /**
         * 开始
         */
        void onStart();

        /**
         * 进度
         *
         * @param progress  进度 0.00 -1.00 ，总大小
         * @param totalSize 总大小 单位B
         */
        void onProgress(float progress, long totalSize);

        /**
         * 总大小
         *
         * @param totalSize 单位B
         */
        void setMax(long totalSize);

        /**
         * 下载完了
         *
         * @param file 下载的app
         * @return true ：下载完自动跳到安装界面，false：则不进行安装
         */
        boolean onFinish(File file);

        /**
         * 下载异常
         *
         * @param msg 异常信息
         */
        void onError(String msg);

        /**
         * 当应用处于前台，准备执行安装程序时候的回调，
         *
         * @param file 当前安装包
         * @return false 默认 false ,当返回时 true 时，需要自己处理 ，前提条件是 onFinish 返回 false 。
         */
        boolean onInstallAppAndAppOnForeground(File file);
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
            //下载
            startDownload(updateApp, callback);
        }

        public void stop(String msg) {
            DownloadService.this.stop(msg);
        }
    }

    class FileDownloadCallBack implements HttpManager.FileCallback {
        private final DownloadCallback mCallBack;
        int oldRate = 0;

        public FileDownloadCallBack(@Nullable DownloadCallback callback) {
            super();
            this.mCallBack = callback;
        }

        @Override
        public void onBefore() {
            //初始化通知栏
            setUpNotification();
            if (mCallBack != null) {
                mCallBack.onStart();
            }
        }

        @Override
        public void onProgress(float progress, long total) {
            //做一下判断，防止自回调过于频繁，造成更新通知栏进度过于频繁，而出现卡顿的问题。
            int rate = Math.round(progress * 100);
            if (oldRate != rate) {
                if (mCallBack != null) {
                    mCallBack.setMax(total);
                    mCallBack.onProgress(progress, total);
                }

                if (mBuilder != null) {
                    mBuilder.setContentTitle("正在下载：" + AppUpdateUtils.getAppName(DownloadService.this))
                            .setContentText(rate + "%")
                            .setProgress(100, rate, false)
                            .setWhen(System.currentTimeMillis());
                    Notification notification = mBuilder.build();
                    notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE;
                    mNotificationManager.notify(NOTIFY_ID, notification);
                }

                //重新赋值
                oldRate = rate;
            }


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
                if (!mCallBack.onFinish(file)) {
                    close();
                    return;
                }
            }

            try {

                if (AppUpdateUtils.isAppOnForeground(DownloadService.this) || mBuilder == null) {
                    //App前台运行
                    mNotificationManager.cancel(NOTIFY_ID);

                    if (mCallBack != null) {
                        boolean temp = mCallBack.onInstallAppAndAppOnForeground(file);
                        if (!temp) {
                            AppUpdateUtils.installApp(DownloadService.this, file);
                        }
                    } else {
                        AppUpdateUtils.installApp(DownloadService.this, file);
                    }


                } else {
                    //App后台运行
                    //更新参数,注意flags要使用FLAG_UPDATE_CURRENT
                    Intent installAppIntent = AppUpdateUtils.getInstallAppIntent(DownloadService.this, file);
                    PendingIntent contentIntent = PendingIntent.getActivity(DownloadService.this, 0, installAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(contentIntent)
                            .setContentTitle(AppUpdateUtils.getAppName(DownloadService.this))
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
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                close();
            }
        }
    }
}
