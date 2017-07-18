package com.vector.appupdatedemo.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;

/**
 * Created by Vector on 2016/8/12 0012.
 */
public class CProgressDialogUtils {
    private static final String TAG = CProgressDialogUtils.class.getSimpleName();
    private static ProgressDialog sCircleProgressDialog;

    private CProgressDialogUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static void showProgressDialog(Activity activity) {
        showProgressDialog(activity, "加载中", false, null);
    }

    public static void showProgressDialog(Activity activity, DialogInterface.OnCancelListener listener) {
        showProgressDialog(activity, "加载中", true, listener);
    }

    public static void showProgressDialog(Activity activity, String msg) {
        showProgressDialog(activity, msg, false, null);
    }

    public static void showProgressDialog(Activity activity, String msg, DialogInterface.OnCancelListener listener) {
        showProgressDialog(activity, msg, true, listener);
    }

    public static void showProgressDialog(final Activity activity, String msg, boolean cancelable, DialogInterface.OnCancelListener listener) {
        if (activity == null || activity.isFinishing()) {
            return;
        }


        if (sCircleProgressDialog == null) {
            sCircleProgressDialog = new ProgressDialog(activity);
            sCircleProgressDialog.setMessage(msg);
            sCircleProgressDialog.setOwnerActivity(activity);
            sCircleProgressDialog.setOnCancelListener(listener);
            sCircleProgressDialog.setCancelable(cancelable);
        } else {
            if (activity.equals(sCircleProgressDialog.getOwnerActivity())) {
                sCircleProgressDialog.setMessage(msg);
                sCircleProgressDialog.setCancelable(cancelable);
                sCircleProgressDialog.setOnCancelListener(listener);
            } else {
                //不相等,所以取消任何ProgressDialog
                cancelProgressDialog();
                sCircleProgressDialog = new ProgressDialog(activity);
                sCircleProgressDialog.setMessage(msg);
                sCircleProgressDialog.setCancelable(cancelable);
                sCircleProgressDialog.setOwnerActivity(activity);
                sCircleProgressDialog.setOnCancelListener(listener);
            }
        }

        if (!sCircleProgressDialog.isShowing()) {
            sCircleProgressDialog.show();
        }

    }


    public static void cancelProgressDialog(Activity activity) {
        if (sCircleProgressDialog != null && sCircleProgressDialog.isShowing()) {
            if (sCircleProgressDialog.getOwnerActivity() == activity) {
                sCircleProgressDialog.cancel();
                sCircleProgressDialog = null;
            }
        }
    }

    public static void cancelProgressDialog() {
        if (sCircleProgressDialog != null && sCircleProgressDialog.isShowing()) {
            sCircleProgressDialog.cancel();
            sCircleProgressDialog = null;
        }
    }

}
