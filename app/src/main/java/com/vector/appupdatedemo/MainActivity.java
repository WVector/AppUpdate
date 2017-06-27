package com.vector.appupdatedemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.RxPermissions;
import com.vector.update_app.DialogActivity;
import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.UpdateCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import rx.functions.Action1;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OkHttpUtils.getInstance()
                .init(this)
                .debug(true, "vector")
                .timeout(20 * 1000);

    }

    public void updateApp(View view) {


        final int color = 0xffeac447;


        String url = "http://47.94.102.201/mobileCard/com/mobile/updateVersion.html";
        String appKey = "ab55ce55Ac4bcP408cPb8c1Aaeac179c5f6f";

        final String targetPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        final UpdateAppHttpUtil httpManager = new UpdateAppHttpUtil();

        UpdateAppManager.updateApp(this, httpManager, url, appKey, new UpdateCallback() {
            @Override
            public void hasNewApp(@Nullable UpdateAppBean updateApp) {
                if (updateApp.isConstraint()) {
                    //强制更新
                } else {
                    //正常更新
                }
                UpdateAppManager.showUpdatedDialog(httpManager, MainActivity.this, targetPath, updateApp, color, R.mipmap.top_test);
            }

            @Override
            public void onAfter() {
                CProgressDialogUtils.cancelProgressDialog(MainActivity.this);
            }

            @Override
            public void noNewApp() {
                Toast.makeText(MainActivity.this, "没有新版本", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBefore() {
                CProgressDialogUtils.showProgressDialog(MainActivity.this);
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
