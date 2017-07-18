//package com.vector.appupdatedemo.ui
//
//import android.os.Bundle
//import android.os.Environment
//import android.support.v7.app.AppCompatActivity
//import com.pawegio.kandroid.toast
//import com.vector.appupdatedemo.R
//import com.vector.appupdatedemo.ext.cancelProgressDialog
//import com.vector.appupdatedemo.ext.showProgressDialog
//import com.vector.appupdatedemo.http.UpdateAppHttpUtil
//import com.vector.update_app.UpdateAppBean
//import com.vector.update_app.utils.Utils
//import com.vector.update_app_kotlin.check
//import com.vector.update_app_kotlin.setSolidTheme
//import com.vector.update_app_kotlin.setStrokeTheme
//import com.vector.update_app_kotlin.updateApp
//import kotlinx.android.synthetic.main.activity_kotlin.*
//import org.json.JSONObject
//import java.util.*
//
//class KotlinActivity : AppCompatActivity() {
//    private val mUpdateUrl = "https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/json/json.txt"
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_kotlin)
//        btn_default.setSolidTheme()
//        btn_diy.setStrokeTheme()
//
//        btn_default.setOnClickListener {
//            defaultUpdate()
//        }
//        btn_diy.setOnClickListener {
//            diyUpdate()
//        }
//    }
//
//    /**
//     * 最简方式
//     */
//    private fun defaultUpdate() {
//        updateApp(mUpdateUrl, UpdateAppHttpUtil())
//                .check {
//                    hasNewApp { _, updateAppManager ->
//                        updateAppManager.showDialog()
//                    }
//                }
//    }
//
//    /**
//     * 自定义
//     */
//    private fun diyUpdate() {
//        //下载路径
//        val path = Environment.getExternalStorageDirectory().absolutePath
//        //自定义参数
//        val params = HashMap<String, String>()
//        params.put("appKey", "ab55ce55Ac4bcP408cPb8c1Aaeac179c5f6f")
//        params.put("appVersion", Utils.getVersionName(this))
//        params.put("key1", "value2")
//        params.put("key2", "value3")
//
//        updateApp(mUpdateUrl, UpdateAppHttpUtil())
//        //自定义配置
//        {
//            //以下设置，都是可选
//            //设置请求方式，默认get
//            setPost(false)
//            //添加自定义参数，默认version=1.0.0（app的versionName）；apkKey=唯一表示（在AndroidManifest.xml配置）
//            setParams(params)
//            //设置点击升级后，消失对话框，默认点击升级后，对话框显示下载进度
//            hideDialogOnDownloading(true)
//            //设置头部，不设置显示默认的图片，设置图片后自动识别主色调，然后为按钮，进度条设置颜色
//            setTopPic(R.mipmap.top_8)
//            //为按钮，进度条设置颜色，默认从顶部图片自动识别。
////                setThemeColor(ColorUtil.getRandomColor())
//            //设置apk下砸路径，默认是在下载到sd卡下/Download/1.0.0/test.apk
//            setTargetPath(path)
//            //设置appKey，默认从AndroidManifest.xml获取，如果，使用自定义参数，则此项无效
////                setAppKey("ab55ce55Ac4bcP408cPb8c1Aaeac179c5f6f")
//
//        }
//                .check {
//                    onBefore { showProgressDialog() }
//                    //自定义解析
//                    parseJson {
//                        val jsonObject = JSONObject(it)
//                        UpdateAppBean()
//                                //（必须）是否更新Yes,No
//                                .setUpdate(jsonObject.optString("update"))
//                                //（必须）新版本号，
//                                .setNewVersion(jsonObject.optString("new_version"))
//                                //（必须）下载地址
//                                .setApkFileUrl(jsonObject.optString("apk_file_url"))
//                                //（必须）更新内容
//                                .setUpdateLog(jsonObject.optString("update_log"))
//                                //大小，不设置不显示大小，可以不设置
//                                .setTargetSize(jsonObject.optString("target_size"))
//                                //是否强制更新，可以不设置
//                                .setConstraint(false)
//                                //设置md5，可以不设置
//                                .setNewMd5(jsonObject.optString("new_md5"))
//
//                    }
//                    hasNewApp { _, updateAppManager ->
//                        updateAppManager.showDialog()
//                    }
//                    noNewApp { toast("没有新版本") }
//                    onAfter { cancelProgressDialog() }
//                }
//
//    }
//
//
//}
