package com.vector.update_app;

import android.support.annotation.Nullable;

/**
 * Created by fengjunming_t on 2016/5/24 0024.
 */
public interface UpdateCallback {
    void isHasNewApp(boolean hasNewApp, @Nullable UpdateAppBean updateApp);

    void onAfter();

    void onBefore();
}
