package com.vector.update_app;

/**
 * Created by fengjunming_t on 2016/5/24 0024.
 */
public interface UpdateCallback {
    void hasNewApp(UpdateAppBean updateApp, UpdateAppManager updateAppManager);

    void onAfter();

    void noNewApp();

    void onBefore();
}
