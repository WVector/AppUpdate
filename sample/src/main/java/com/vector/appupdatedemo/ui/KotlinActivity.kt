package com.vector.appupdatedemo.ui

import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import com.vector.appupdatedemo.R
import com.vector.appupdatedemo.ext.cancelProgressDialog
import com.vector.appupdatedemo.ext.dialog
import com.vector.appupdatedemo.ext.showProgressDialog
import com.vector.appupdatedemo.ext.toast
import com.vector.appupdatedemo.http.UpdateAppHttpUtil
import com.vector.appupdatedemo.util.HProgressDialogUtils
import com.vector.update_app.SilenceUpdateCallback
import com.vector.update_app.UpdateAppBean
import com.vector.update_app.UpdateAppManager
import com.vector.update_app.utils.AppUpdateUtils
import com.vector.update_app_kotlin.*
import kotlinx.android.synthetic.main.activity_kotlin.*
import org.json.JSONObject
import java.io.File
import java.util.*

class KotlinActivity : AppCompatActivity() {
    private val mUpdateUrl = "https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/json/json.txt"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)
        btn_default.setSolidTheme()
        btn_diy_1.setStrokeTheme()
        btn_diy_2.setStrokeTheme()
        btn_diy_3.setStrokeTheme()
        btn_diy_4.setStrokeTheme()
        btn_diy_5.setStrokeTheme()

        btn_default.setOnClickListener {
            defaultUpdate()
        }
        btn_diy_1.setOnClickListener {
            updateDiy1()
        }

        btn_diy_2.setOnClickListener {
            updateDiy2()
        }
        btn_diy_3.setOnClickListener {
            updateDiy3()
        }
        btn_diy_4.setOnClickListener {
            updateDiy4()
        }
        btn_diy_5.setOnClickListener {
            updateDiy5()
        }
    }


    private var isShowDownloadProgress: Boolean = false
    /**
     * 最简方式
     */
    private fun defaultUpdate() {
        updateApp(mUpdateUrl, UpdateAppHttpUtil()).update()
    }

    /**
     * 自定义接口协议+自定义对话框
     */
    private fun updateDiy2() {
        isShowDownloadProgress = false
        diy()
    }


    /**
     * 自定义接口协议+自定义对话框+显示进度对话框
     */
    private fun updateDiy3() {
        isShowDownloadProgress = true
        diy()
    }

    private fun diy() {
        //下载路径
        val path = Environment.getExternalStorageDirectory().absolutePath
        //自定义参数
        val params = HashMap<String, String>()
        params.put("appKey", "ab55ce55Ac4bcP408cPb8c1Aaeac179c5f6f")
        params.put("appVersion", AppUpdateUtils.getVersionName(this))
        params.put("key1", "value2")
        params.put("key2", "value3")

        updateApp(mUpdateUrl, UpdateAppHttpUtil())
        //自定义配置
        {
            //以下设置，都是可选
            //设置请求方式，默认get
            isPost = false
            //添加自定义参数，默认version=1.0.0（app的versionName）；apkKey=唯一表示（在AndroidManifest.xml配置）
            setParams(params)
            //设置apk下砸路径，默认是在下载到sd卡下/Download/1.0.0/test.apk
            targetPath = path
            //设置appKey，默认从AndroidManifest.xml获取，如果，使用自定义参数，则此项无效
//                setAppKey("ab55ce55Ac4bcP408cPb8c1Aaeac179c5f6f")

        }
                .check {
                    onBefore { showProgressDialog() }
                    //自定义解析
                    parseJson {
                        val jsonObject = JSONObject(it)
                        UpdateAppBean()
                                //（必须）是否更新Yes,No
                                .setUpdate(jsonObject.optString("update"))
                                //（必须）新版本号，
                                .setNewVersion(jsonObject.optString("new_version"))
                                //（必须）下载地址
                                .setApkFileUrl(jsonObject.optString("apk_file_url"))
                                //（必须）更新内容
                                .setUpdateLog(jsonObject.optString("update_log"))
                                //大小，不设置不显示大小，可以不设置
                                .setTargetSize(jsonObject.optString("target_size"))
                                //是否强制更新，可以不设置
                                .setConstraint(false)
                                //设置md5，可以不设置
                                .setNewMd5(jsonObject.optString("new_md5"))

                    }

                    hasNewApp { updateApp, updateAppManager ->
                        showDiyDialog(updateApp, updateAppManager)
                    }
                    noNewApp { toast("没有新版本") }
                    onAfter { cancelProgressDialog() }
                }
    }

    /**
     * 自定义对话框
     */
    private fun showDiyDialog(updateApp: UpdateAppBean, updateAppManager: UpdateAppManager) {
        dialog("是否升级到${updateApp.newVersion}版本？"
                , "新版本大小：${updateApp.targetSize}\n\n${updateApp.updateLog}")
        {
            positiveButton("升级") {
                if (isShowDownloadProgress) {
                    updateAppManager.download {
                        onStart { HProgressDialogUtils.showHorizontalProgressDialog(this@KotlinActivity, "下载进度", false) }
                        onProgress { progress, _ -> HProgressDialogUtils.setProgress(Math.round(progress * 100)) }
                        onFinish {
                            HProgressDialogUtils.cancel()
                            true
                        }
                        onError {
                            toast(it)
                            HProgressDialogUtils.cancel()
                        }
                    }

                } else {
                    updateAppManager.download()
                }

                dismiss()

            }
            negativeButton("暂不升级") {
                dismiss()
            }
            show()
        }

    }


    /**
     * 自定义接口协议
     */
    private fun updateDiy1() {
        //下载路径
        val path = Environment.getExternalStorageDirectory().absolutePath
        //自定义参数
        val params = HashMap<String, String>()
        params.put("appKey", "ab55ce55Ac4bcP408cPb8c1Aaeac179c5f6f")
        params.put("appVersion", AppUpdateUtils.getVersionName(this))
        params.put("key1", "value2")
        params.put("key2", "value3")

        updateApp(mUpdateUrl, UpdateAppHttpUtil())
        //自定义配置
        {
            //以下设置，都是可选
            //设置请求方式，默认get
            isPost = false
            //添加自定义参数，默认version=1.0.0（app的versionName）；apkKey=唯一表示（在AndroidManifest.xml配置）
            setParams(params)
            //设置点击升级后，消失对话框，默认点击升级后，对话框显示下载进度
            hideDialogOnDownloading()
            //设置头部，不设置显示默认的图片，设置图片后自动识别主色调，然后为按钮，进度条设置颜色
            topPic = R.mipmap.top_8

            //为按钮，进度条设置颜色。
            themeColor = 0xffffac5d.toInt()
            //设置apk下砸路径，默认是在下载到sd卡下/Download/1.0.0/test.apk
            targetPath = path
            //设置appKey，默认从AndroidManifest.xml获取，如果，使用自定义参数，则此项无效
//                setAppKey("ab55ce55Ac4bcP408cPb8c1Aaeac179c5f6f")

        }
                .check {
                    onBefore { showProgressDialog() }
                    //自定义解析
                    parseJson {
                        val jsonObject = JSONObject(it)
                        UpdateAppBean()
                                //（必须）是否更新Yes,No
                                .setUpdate(jsonObject.optString("update"))
                                //（必须）新版本号，
                                .setNewVersion(jsonObject.optString("new_version"))
                                //（必须）下载地址
                                .setApkFileUrl(jsonObject.optString("apk_file_url"))
                                //（必须）更新内容
                                .setUpdateLog(jsonObject.optString("update_log"))
                                //大小，不设置不显示大小，可以不设置
                                .setTargetSize(jsonObject.optString("target_size"))
                                //是否强制更新，可以不设置
                                .setConstraint(false)
                                //设置md5，可以不设置
                                .setNewMd5(jsonObject.optString("new_md5"))

                    }
                    noNewApp { toast("没有新版本") }
                    onAfter { cancelProgressDialog() }
                }

    }

    /**
     * 静默下载
     */
    private fun updateDiy4() {
        updateApp(mUpdateUrl, UpdateAppHttpUtil())
        {
            setOnlyWifi()
        }.silenceUpdate()
    }

    /**
     * 静默下载+自定义对话框
     */
    private fun updateDiy5() {
        updateApp(mUpdateUrl, UpdateAppHttpUtil())
        {
            setOnlyWifi()
        }.checkNewApp(object : SilenceUpdateCallback() {
            override fun showDialog(updateApp: UpdateAppBean, updateAppManager: UpdateAppManager?, appFile: File) {
                showSilenceDiyDialog(updateApp, appFile)
            }
        })
    }

    /**
     * 自定义对话框
     */
    private fun showSilenceDiyDialog(updateApp: UpdateAppBean, appFile: File?) {
        dialog("是否升级到${updateApp.newVersion}版本？"
                , "新版本大小：${updateApp.targetSize}\n\n${updateApp.updateLog}")
        {
            positiveButton("升级") {
                AppUpdateUtils.installApp(this@KotlinActivity, appFile)
                dismiss()

            }
            negativeButton("暂不升级") {
                dismiss()
            }
            show()
        }
    }


}
