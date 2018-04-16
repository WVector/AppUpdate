package com.vector.update_app.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.listener.ExceptionHandler;
import com.vector.update_app.listener.ExceptionHandlerHelper;

import java.io.File;
import java.util.List;

/**
 * Created by Vector
 * on 2017/6/6 0006.
 */

public class AppUpdateUtils {


    public static final String IGNORE_VERSION = "ignore_version";
    private static final String PREFS_FILE = "update_app_config.xml";
    public static final int REQ_CODE_INSTALL_APP = 99;

    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.getType() == ConnectivityManager.TYPE_WIFI;
    }


    public static File getAppFile(UpdateAppBean updateAppBean) {
        String appName = getApkName(updateAppBean);
        return new File(updateAppBean.getTargetPath()
                .concat(File.separator + updateAppBean.getNewVersion())
                .concat(File.separator + appName));
    }

    @NonNull
    public static String getApkName(UpdateAppBean updateAppBean) {
        String apkUrl = updateAppBean.getApkFileUrl();
        String appName = apkUrl.substring(apkUrl.lastIndexOf("/") + 1, apkUrl.length());
        if (!appName.endsWith(".apk")) {
            appName = "temp.apk";
        }
        return appName;
    }

    public static boolean appIsDownloaded(UpdateAppBean updateAppBean) {
        //md5不为空
        //文件存在
        //md5只一样
        File appFile = getAppFile(updateAppBean);
        return !TextUtils.isEmpty(updateAppBean.getNewMd5())
                && appFile.exists()
                && Md5Util.getFileMD5(appFile).equalsIgnoreCase(updateAppBean.getNewMd5());
    }


    public static boolean installApp(Context context, File appFile) {
        try {
            Intent intent = getInstallAppIntent(context, appFile);
            if (context.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
                context.startActivity(intent);

            }
            return true;
        } catch (Exception e) {
            ExceptionHandler exceptionHandler = ExceptionHandlerHelper.getInstance();
            if (exceptionHandler != null) {
                exceptionHandler.onException(e);
            }
        }
        return false;
    }

    public static boolean installApp(Activity activity, File appFile) {
        try {
            Intent intent = getInstallAppIntent(activity, appFile);
            if (activity.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
                activity.startActivityForResult(intent, REQ_CODE_INSTALL_APP);
            }
            return true;
        } catch (Exception e) {
            ExceptionHandler exceptionHandler = ExceptionHandlerHelper.getInstance();
            if (exceptionHandler != null) {
                exceptionHandler.onException(e);
            }
        }
        return false;
    }

    public static boolean installApp(Fragment fragment, File appFile) {
        return installApp(fragment.getActivity(), appFile);
    }

    public static Intent getInstallAppIntent(Context context, File appFile) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //区别于 FLAG_GRANT_READ_URI_PERMISSION 跟 FLAG_GRANT_WRITE_URI_PERMISSION， URI权限会持久存在即使重启，直到明确的用 revokeUriPermission(Uri, int) 撤销。 这个flag只提供可能持久授权。但是接收的应用必须调用ContentResolver的takePersistableUriPermission(Uri, int)方法实现
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileProvider", appFile);
                intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(appFile), "application/vnd.android.package-archive");
            }
            return intent;
        } catch (Exception e) {
            ExceptionHandler exceptionHandler = ExceptionHandlerHelper.getInstance();
            if (exceptionHandler != null) {
                exceptionHandler.onException(e);
            }
        }
        return null;
    }

    public static String getVersionName(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        if (packageInfo != null) {
            return packageInfo.versionName;
        }
        return "";
    }

    public static int getVersionCode(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        if (packageInfo != null) {
            return packageInfo.versionCode;
        }
        return 0;
    }

    public static PackageInfo getPackageInfo(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isAppOnForeground(Context context) {

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {

            if (appProcess.processName.equals(packageName) && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }

    public static String getAppName(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        if (packageInfo != null) {
            return packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
        }
        return "";
    }

    public static Drawable getAppIcon(Context context) {
        try {
            return context.getPackageManager().getApplicationIcon(context.getPackageName());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {


        Bitmap bitmap = Bitmap.createBitmap(

                drawable.getIntrinsicWidth(),

                drawable.getIntrinsicHeight(),

                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888

                        : Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);

        //canvas.setBitmap(bitmap);

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        drawable.draw(canvas);

        return bitmap;

    }

    public static int dip2px(int dip, Context context) {
        return (int) (dip * getDensity(context) + 0.5f);
    }

    public static float getDensity(Context context) {
        return getDisplayMetrics(context).density;
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    public static String getManifestString(Context context, String name) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return appInfo.metaData.getString(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static SharedPreferences getSP(Context context) {
        return context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
    }

    public static void saveIgnoreVersion(Context context, String newVersion) {
        getSP(context).edit().putString(IGNORE_VERSION, newVersion).apply();
    }

    public static boolean isNeedIgnore(Context context, String newVersion) {
        return getSP(context).getString(IGNORE_VERSION, "").equals(newVersion);
    }
}
