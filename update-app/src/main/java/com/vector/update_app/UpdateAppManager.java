package com.vector.update_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fengjunming_t on 2016/5/24 0024.
 */
public class UpdateAppManager {
    final static String INTENT_KEY = "update_dialog_values";
    private static final String TAG = UpdateAppManager.class.getSimpleName();
    private static UpdateAppManager sUpdateAppManager = null;

    private UpdateAppManager() {
    }

    public static UpdateAppManager getInstance() {
        if (sUpdateAppManager == null) {
            sUpdateAppManager = new UpdateAppManager();
        }
        return sUpdateAppManager;
    }

    /**
     * 跳转到更新页面
     *
     * @param updateApp
     */
    public void showUpdatedDialog(HttpManager httpManager, Activity context, String targetPath, UpdateAppBean updateApp) {

        String preSuffix = "/storage/emulated";

        if (TextUtils.isEmpty(targetPath) || !targetPath.startsWith(preSuffix)) {
            Log.e(TAG, "下载路径错误:" + targetPath);
            return;
        }
        if (updateApp == null) {
            return;
        }

        if (context != null && !context.isFinishing()) {
            Intent updateIntent = new Intent(context, NewDialogActivity.class);
            updateApp.setTargetPath(targetPath);
            updateApp.setHttpManager(httpManager);
            updateIntent.putExtra(INTENT_KEY, updateApp);
            context.startActivity(updateIntent);
        }

    }

    /**
     * 检测是否有新版本
     *
     * @param callback
     */
    public void updateApp(Context context, HttpManager httpManager, String updateUrl, String appKey, final UpdateCallback callback) {


        if (callback == null) {
            return;
        }

        callback.onBefore();

        if (UpdateDialogActivity.isShow) {
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
    private void processData(String result, @NonNull UpdateCallback callback) {
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
