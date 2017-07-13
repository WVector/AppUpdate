package com.vector.appupdatedemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.RxPermissions;
import com.vector.update_app.DialogActivity;
import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.UpdateCallback;
import com.vector.update_app.utils.DrawableUtil;
import com.vector.update_app.utils.Utils;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import rx.functions.Action1;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private int color2 = 0xffe939dc;
    private int color3 = 0xff39c1e9;
    //默认
    private int color1 = 0xffe94339;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OkHttpUtils.getInstance()
                .init(this)
                .debug(true, "okHttp")
                .timeout(20 * 1000);

        DrawableUtil.setTextStrokeTheme((Button) findViewById(R.id.btn_get_permission));
        DrawableUtil.setTextStrokeTheme((Button) findViewById(R.id.btn_update_app_4));
        DrawableUtil.setTextStrokeTheme((Button) findViewById(R.id.btn_update_app), color1);
        DrawableUtil.setTextStrokeTheme((Button) findViewById(R.id.btn_update_app_2), color2);
        DrawableUtil.setTextStrokeTheme((Button) findViewById(R.id.btn_update_app_3), color3);
        DrawableUtil.setTextSolidTheme((Button) findViewById(R.id.btn_test));

        ImageView im = (ImageView) findViewById(R.id.iv);

        im.setImageBitmap(Utils.drawableToBitmap(Utils.getAppIcon(this)));

    }

    public void updateApp(View view) {
        String updateUrl = "https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/json/json.txt";
        new UpdateAppManager
                .Builder()
                //当前Activity
                .setActivity(this)
                //实现httpManager接口的对象
                .setHttpManager(new UpdateAppHttpUtil())
                //更新地址
                .setUpdateUrl(updateUrl)
                .build()
                //检测是否有新版本
                .checkNewApp(new UpdateCallback() {
                    /**
                     * 有新版本
                     *
                     * @param updateApp        新版本信息
                     * @param updateAppManager app更新管理器
                     */
                    @Override
                    public void hasNewApp(UpdateAppBean updateApp, UpdateAppManager updateAppManager) {
                        updateAppManager.showDialog();
                    }

                    /**
                     * 网络请求之前
                     */
                    @Override
                    public void onBefore() {
                        CProgressDialogUtils.showProgressDialog(MainActivity.this);
                    }

                    /**
                     * 网路请求之后
                     */
                    @Override
                    public void onAfter() {
                        CProgressDialogUtils.cancelProgressDialog(MainActivity.this);
                    }

                    /**
                     * 没有新版本
                     */

                    @Override
                    public void noNewApp() {
                        Toast.makeText(MainActivity.this, "没有新版本", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void updateApp2(View view) {
        String updateUrl = "https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/json/json.txt";
        new UpdateAppManager
                .Builder()
                .setActivity(this)
                .setHttpManager(new UpdateAppHttpUtil())
                .setUpdateUrl(updateUrl)
                //自定义对话框头部图片
                .setTopPic(R.mipmap.top_2)
                //设置主题色
                .setThemeColor(color2)
                .build()
                .checkNewApp(new UpdateCallback() {
                    @Override
                    public void hasNewApp(UpdateAppBean updateApp, UpdateAppManager updateAppManager) {
                        updateAppManager.showDialog();
                    }

                    @Override
                    public void onBefore() {
                        CProgressDialogUtils.showProgressDialog(MainActivity.this);
                    }

                    @Override
                    public void onAfter() {
                        CProgressDialogUtils.cancelProgressDialog(MainActivity.this);
                    }

                    @Override
                    public void noNewApp() {
                        Toast.makeText(MainActivity.this, "没有新版本", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void updateApp3(View view) {
        String updateUrl = "https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/json/json.txt";
        new UpdateAppManager
                .Builder()
                .setActivity(this)
                .setHttpManager(new UpdateAppHttpUtil())
                .setUpdateUrl(updateUrl)
                //自定义对话框头部图片
                .setTopPic(R.mipmap.top_3)
                //设置主题色
                .setThemeColor(color3)
                .build()
                .checkNewApp(new UpdateCallback() {
                    @Override
                    public void hasNewApp(UpdateAppBean updateApp, UpdateAppManager updateAppManager) {
                        updateAppManager.showDialog();
                    }

                    @Override
                    public void onBefore() {
                        CProgressDialogUtils.showProgressDialog(MainActivity.this);
                    }

                    @Override
                    public void onAfter() {
                        CProgressDialogUtils.cancelProgressDialog(MainActivity.this);
                    }

                    @Override
                    public void noNewApp() {
                        Toast.makeText(MainActivity.this, "没有新版本", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void getPermission(View view) {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(WRITE_EXTERNAL_STORAGE)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            Toast.makeText(MainActivity.this, "已授权", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "未授权", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


    public void test(View view) {
        startActivity(new Intent(this, DialogActivity.class));
    }

    public void updateApp4(View view) {

        Map<String, String> params = new HashMap<String, String>();

        params.put("key1", "value1");
        params.put("key2", "value2");
        params.put("key3", "value3");
        params.put("key4", "value4");


        new UpdateAppManager
                .Builder()
                //当前Activity
                .setActivity(this)
                //实现httpManager接口的对象
                .setHttpManager(new UpdateAppHttpUtil())
                //设置请求方式 默认get,
                .setPost(false)
                //更新地址
                .setUpdateUrl("https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/json/json.txt")
                //添加自定义参数
                .setParams(params)
                //设置点击升级后，消失对话框
                .hideDialogOnDownloading(true)
                //设置头部
                .setTopPic(R.mipmap.top_8)
                //设置主题色
//                .setThemeColor(0xff034ea0)
                .build()
                //检测是否有新版本
                .checkNewApp(new UpdateCallback() {
                    /**
                     * 解析json,自定义协议
                     *
                     * @param json 服务器返回的json
                     * @return UpdateAppBean
                     */
                    @Override
                    protected UpdateAppBean parseJson(String json) {
                        UpdateAppBean updateAppBean = new UpdateAppBean();
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            updateAppBean
                                    //是否更新Yes,No
                                    .setUpdate(jsonObject.optString("update"))
                                    //新版本号
                                    .setNewVersion(jsonObject.optString("new_version"))
                                    //下载地址
                                    .setApkFileUrl(jsonObject.optString("apk_file_url"))
                                    //大小
                                    .setTargetSize(jsonObject.optString("target_size"))
                                    //更新内容 测试更新内容过多
                                    .setUpdateLog("1，添加删除信用卡接口\n2，添加vip认证\n3，区分自定义消费，一个小时不限制。\n4，添加放弃任务接口，小时内不生成。\n5，消费任务手动生成。" +
                                            "\n1，添加删除信用卡接口\n2，添加vip认证\n3，区分自定义消费，一个小时不限制。\n4，添加放弃任务接口，小时内不生成。\n5，消费任务手动生成。" +
                                            "\n1，添加删除信用卡接口\n2，添加vip认证\n3，区分自定义消费，一个小时不限制。\n4，添加放弃任务接口，小时内不生成。\n5，消费任务手动生成。" +
                                            "\n1，添加删除信用卡接口\n2，添加vip认证\n3，区分自定义消费，一个小时不限制。\n4，添加放弃任务接口，小时内不生成。\n5，消费任务手动生成。" +
                                            "\n1，添加删除信用卡接口\n2，添加vip认证\n3，区分自定义消费，一个小时不限制。\n4，添加放弃任务接口，小时内不生成。\n5，消费任务手动生成。" +
                                            "\n1，添加删除信用卡接口\n2，添加vip认证\n3，区分自定义消费，一个小时不限制。\n4，添加放弃任务接口，小时内不生成。\n5，消费任务手动生成。")
                                    //是否强制更新
                                    .setConstraint(false);
                            //设置md5
//                                    .setNewMd5(jsonObject.optString("new_md5ddfdfdf"));
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
                    @Override
                    public void hasNewApp(UpdateAppBean updateApp, UpdateAppManager updateAppManager) {
                        if (updateApp.isConstraint()) {

                        } else {

                        }
                        updateAppManager.showDialog();
                    }

                    /**
                     * 网络请求之前
                     */
                    @Override
                    public void onBefore() {
                        CProgressDialogUtils.showProgressDialog(MainActivity.this);
                    }

                    /**
                     * 网路请求之后
                     */
                    @Override
                    public void onAfter() {
                        CProgressDialogUtils.cancelProgressDialog(MainActivity.this);
                    }

                    /**
                     * 没有新版本
                     */
                    @Override
                    public void noNewApp() {
                        Toast.makeText(MainActivity.this, "没有新版本", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}
