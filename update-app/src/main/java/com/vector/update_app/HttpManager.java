package com.vector.update_app;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.Serializable;
import java.util.Map;


public interface HttpManager extends Serializable {
    void postSync(@NonNull String url, @NonNull Map<String, String> params, @NonNull Callback callBack);

    void download(@NonNull String url, @NonNull String path, @NonNull String fileName, @NonNull FileCallback callback);

    interface FileCallback {
        void onProgress(float progress, long total);

        void onError(String error);

        void onResponse(File file);

        void onBefore();
    }

    interface Callback {
        void onResponse(String result);

        void onError(String error);
    }
}
