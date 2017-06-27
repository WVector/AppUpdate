package com.vector.update_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.vector.update_app.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fengjunming_t on 2016/5/24 0024.
 */
public class UpdateAppManager {
    final static String INTENT_KEY = "update_dialog_values";
    final static String THEME_KEY = "theme_color";
    final static String TOP_IMAGE_KEY = "top_resId";
    private static final String TAG = UpdateAppManager.class.getSimpleName();

    private UpdateAppManager() {
    }

    /**
     * 跳转到更新页面
     *
     * @param updateApp
     */
    public static void showUpdatedDialog(HttpManager httpManager, Activity context, String targetPath, UpdateAppBean updateApp, int themeColor, @DrawableRes int resId) {

        String preSuffix = "/storage/emulated";

        if (TextUtils.isEmpty(targetPath) || !targetPath.startsWith(preSuffix)) {
            Log.e(TAG, "下载路径错误:" + targetPath);
            return;
        }
        if (updateApp == null) {
            return;
        }

        if (context != null && !context.isFinishing()) {
            Intent updateIntent = new Intent(context, DialogActivity.class);
            updateApp.setTargetPath(targetPath);
            updateApp.setHttpManager(httpManager);
            updateIntent.putExtra(INTENT_KEY, updateApp);
            if (themeColor != 0) {
                updateIntent.putExtra(THEME_KEY, themeColor);
            }

            if (resId != 0) {
                updateIntent.putExtra(TOP_IMAGE_KEY, resId);
            }
            context.startActivity(updateIntent);
        }

    }

    public static void showUpdatedDialog(HttpManager httpManager, Activity context, String targetPath, UpdateAppBean updateApp) {

        showUpdatedDialog(httpManager, context, targetPath, updateApp, 0, 0);
    }

    /**
     * 检测是否有新版本
     *
     * @param callback
     */
    public static void updateApp(Context context, HttpManager httpManager, String updateUrl, String appKey, final UpdateCallback callback) {


        if (callback == null) {
            return;
        }

        callback.onBefore();

        if (DialogActivity.isShow) {
            callback.onAfter();
            return;
        }
        //拼接参数

        Map<String, String> params = new HashMap<String, String>();
        params.put("appKey", appKey);
        String versionName = Utils.getVersionName(context);
        if (versionName.endsWith("-debug")) {
            versionName = versionName.substring(0, versionName.lastIndexOf('-'));
        }
        params.put("version", versionName);
        httpManager.asyncPost(updateUrl, params, new HttpManager.Callback() {
            @Override
            public void onResponse(String result) {
                callback.onAfter();
                if (result != null) {
                    processData(result, callback);
                }
            }

            @Override
            public void onError(String error) {
                callback.onAfter();
                callback.noNewApp();
            }
        });
    }

    /**
     * 解析
     *
     * @param result
     * @param callback
     */
    private static void processData(String result, @NonNull UpdateCallback callback) {
        try {
            UpdateAppBean updateApp = JSON.parseObject(result, UpdateAppBean.class);
            if (updateApp.isSucceed()) {
                if (updateApp.isUpdate()) {
                    callback.hasNewApp(updateApp);
                    //跳转到升级界面
//                    showUpdatedDialog(updateApp);
                } else {
                    callback.noNewApp();
                }
            } else {
                callback.noNewApp();
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
            callback.noNewApp();
        }
    }

}
