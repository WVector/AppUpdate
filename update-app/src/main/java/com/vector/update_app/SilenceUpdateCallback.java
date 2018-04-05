package com.vector.update_app;

import com.vector.update_app.service.DownloadService;
import com.vector.update_app.utils.AppUpdateUtils;

import java.io.File;

/**
 * Created by Vector
 * on 2017/7/20 0020.
 */

public class SilenceUpdateCallback extends UpdateCallback {
    @Override
    protected final void hasNewApp(final UpdateAppBean updateApp, final UpdateAppManager updateAppManager) {
        //添加信息
        UpdateAppBean updateAppBean = updateAppManager.fillUpdateAppData();
        //设置不显示通知栏下载进度
        updateAppBean.dismissNotificationProgress(true);

        if (AppUpdateUtils.appIsDownloaded(updateApp)) {
            showDialog(updateApp, updateAppManager, AppUpdateUtils.getAppFile(updateApp));
        } else {
            //假如是onlyWifi,则进行判断网络环境
            if (updateApp.isOnlyWifi() && !AppUpdateUtils.isWifi(updateAppManager.getContext())) {
                //要求是wifi下，且当前不是wifi环境
                return;
            }
            updateAppManager.download(new DownloadService.DownloadCallback() {
                @Override
                public void onStart() {

                }

                @Override
                public void onProgress(float progress, long totalSize) {

                }

                @Override
                public void setMax(long totalSize) {

                }

                @Override
                public boolean onFinish(File file) {
                    showDialog(updateApp, updateAppManager, file);
                    return false;
                }


                @Override
                public void onError(String msg) {

                }

                @Override
                public boolean onInstallAppAndAppOnForeground(File file) {
                    return false;
                }
            });
        }
    }

    /**
     * 使用默认对话框，
     *
     * @param updateApp        新app信息
     * @param updateAppManager 网路接口
     * @param appFile          下载好的app文件
     */
    protected void showDialog(UpdateAppBean updateApp, UpdateAppManager updateAppManager, File appFile) {
        updateAppManager.showDialogFragment();
    }
}
