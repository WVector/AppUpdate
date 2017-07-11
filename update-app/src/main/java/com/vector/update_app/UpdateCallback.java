package com.vector.update_app;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 新版本版本检测回调
 */
public abstract class UpdateCallback {

    /**
     * 解析json,自定义协议
     *
     * @param json 服务器返回的json
     * @return UpdateAppBean
     */
    protected UpdateAppBean parseJson(String json) {
        UpdateAppBean updateAppBean = new UpdateAppBean();
        try {
            JSONObject jsonObject = new JSONObject(json);
            updateAppBean.setUpdate(jsonObject.getString("update"))
                    .setNewVersion(jsonObject.getString("new_version"))
                    .setApkFileUrl(jsonObject.getString("apk_file_url"))
                    .setTargetSize(jsonObject.getString("target_size"))
                    .setUpdateLog(jsonObject.getString("update_log"))
                    .setConstraint(jsonObject.getBoolean("constraint"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return updateAppBean;
    }

    /**
     * 有新版本
     *
     * @param updateApp        新版本信息
     * @param updateAppManager app更新管理器
     */
    protected abstract void hasNewApp(UpdateAppBean updateApp, UpdateAppManager updateAppManager);

    /**
     * 网路请求之后
     */
    protected abstract void onAfter();

    /**
     * 没有新版本
     */
    protected abstract void noNewApp();

    /**
     * 网络请求之前
     */
    protected abstract void onBefore();

}
