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
                .setActivity(this)
                .setHttpManager(new UpdateAppHttpUtil())
                .setUpdateUrl(updateUrl)
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

    public void updateApp2(View view) {
        String updateUrl = "https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/json/json.txt";
        new UpdateAppManager
                .Builder()
                .setActivity(this)
                .setHttpManager(new UpdateAppHttpUtil())
                .setUpdateUrl(updateUrl)
                .setTopPic(R.mipmap.top_2)
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
                .setTopPic(R.mipmap.top_3)
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
}
