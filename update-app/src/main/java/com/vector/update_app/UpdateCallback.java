package com.vector.update_app;

/**
 * 新版本版本检测回调
 */
public interface UpdateCallback {
    /**
     * 有新版本
     *
     * @param updateApp        新版本信息
     * @param updateAppManager app更新管理器
     */
    void hasNewApp(UpdateAppBean updateApp, UpdateAppManager updateAppManager);

    /**
     * 网路请求之后
     */
    void onAfter();

    /**
     * 没有新版本
     */
    void noNewApp();

    /**
     * 网络请求之前
     */
    void onBefore();
}
