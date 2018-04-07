package com.vector.update_app_kotlin

import android.app.Activity
import com.vector.update_app.HttpManager
import com.vector.update_app.UpdateAppBean
import com.vector.update_app.UpdateAppManager
import com.vector.update_app.UpdateCallback
import com.vector.update_app.service.DownloadService
import java.io.File

/**
 * Created by Vector
 * on 2017/7/18 0018.
 */
fun Activity.updateApp(
        updateUrl_: String,
        httpManager_: HttpManager,
        init: (UpdateAppManager.Builder.() -> Unit)? = null): UpdateAppManager {
    val act = this
    return UpdateAppManager.Builder().apply {
        activity = act
        httpManager = httpManager_
        updateUrl = updateUrl_
        if (init != null) init()
    }.build()
}

inline fun UpdateAppManager.check(init: Callback.() -> Unit) {
    checkNewApp(Callback().apply(init))
}

class Callback : UpdateCallback() {
    private var _onBefore: (() -> Unit)? = null
    private var _noNewApp: (() -> Unit)? = null
    private var _onAfter: (() -> Unit)? = null
    private var _parseJson: ((json: String?) -> UpdateAppBean)? = null
    private var _hasNewApp: ((updateApp: UpdateAppBean, updateAppManager: UpdateAppManager) -> Unit)? = null

    override fun parseJson(json: String?): UpdateAppBean {
        if (_parseJson != null) {
            return _parseJson!!.invoke(json)
        }
        return super.parseJson(json)
    }

    override fun onBefore() {
        _onBefore?.invoke()
    }

    override fun hasNewApp(updateApp: UpdateAppBean, updateAppManager: UpdateAppManager) {
        if (_hasNewApp != null) {
            _hasNewApp?.invoke(updateApp, updateAppManager)
        } else {
            super.hasNewApp(updateApp, updateAppManager)
        }
    }

    override fun noNewApp(error: String) {
        _noNewApp?.invoke()
    }

    override fun onAfter() {
        _onAfter?.invoke()
    }


    fun onBefore(listener: () -> Unit) {
        _onBefore = listener
    }

    fun onAfter(listener: () -> Unit) {
        _onAfter = listener
    }

    fun noNewApp(listener: () -> Unit) {
        _noNewApp = listener
    }

    fun hasNewApp(listener: (updateApp: UpdateAppBean, updateAppManager: UpdateAppManager) -> Unit) {
        _hasNewApp = listener
    }

    fun parseJson(listener: (json: String?) -> UpdateAppBean) {
        _parseJson = listener
    }
}

inline fun UpdateAppManager.download(init: DownloadCallback.() -> Unit) {
    download(DownloadCallback().apply(init))
}

class DownloadCallback : DownloadService.DownloadCallback {


    private var _onStart: (() -> Unit)? = null
    private var _onFinish: (() -> Boolean)? = null
    private var _onError: ((msg: String) -> Unit)? = null
    private var _setMax: ((totalSize: Long) -> Unit)? = null
    private var _onInstallAppAndAppOnForeground: ((file: File) -> Boolean)? = null
    private var _onProgress: ((progress: Float, totalSize: Long) -> Unit)? = null

    override fun onStart() {
        _onStart?.invoke()
    }

    override fun onProgress(progress: Float, totalSize: Long) {
        _onProgress?.invoke(progress, totalSize)
    }

    override fun setMax(totalSize: Long) {
        _setMax?.invoke(totalSize)
    }


    override fun onFinish(file: File?): Boolean {
        if (_onFinish != null) {
            return _onFinish!!.invoke()
        } else {
            return true
        }
    }

    override fun onError(msg: String) {
        _onError?.invoke(msg)
    }

    override fun onInstallAppAndAppOnForeground(file: File?): Boolean {

        return _onInstallAppAndAppOnForeground?.invoke(file!!)!!
    }

    fun onStart(listener: () -> Unit) {
        _onStart = listener
    }

    fun onFinish(listener: () -> Boolean) {
        _onFinish = listener
    }

    fun onError(listener: (msg: String) -> Unit) {
        _onError = listener
    }

    fun setMax(listener: (totalSize: Long) -> Unit) {
        _setMax = listener
    }

    fun onProgress(listener: (progress: Float, totalSize: Long) -> Unit) {
        _onProgress = listener
    }

    fun onInstallAppAndAppOnForeground(listener: (file: File) -> Boolean) {
        _onInstallAppAndAppOnForeground = listener
    }
}