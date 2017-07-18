package com.vector.update_app_kotlin

import android.app.Activity
import com.vector.update_app.HttpManager
import com.vector.update_app.UpdateAppBean
import com.vector.update_app.UpdateAppManager
import com.vector.update_app.UpdateCallback

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
        _hasNewApp?.invoke(updateApp, updateAppManager)
    }

    override fun noNewApp() {
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