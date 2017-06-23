package com.vector.update_app;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vector.update_app.service.DownloadService;
import com.vector.update_app.view.NumberProgressBar;

public class NewDialogActivity extends FragmentActivity implements View.OnClickListener {


    public static boolean isShow = false;
    private TextView mContentTextView;
    private Button mUpdateOkButton;
    private UpdateAppBean mUpdateApp;
    private NumberProgressBar mNumberProgressBar;
    private ImageView mIvClose;
    private TextView mTitleTextView;
    /**
     * 回调
     */
    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            startDownloadApp((DownloadService.DownloadBinder) service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isShow = true;
        setContentView(R.layout.new_update_dialog);
        initView();
        initData();
    }

    private void initView() {
        mContentTextView = (TextView) findViewById(R.id.tv_update_info);
        mTitleTextView = (TextView) findViewById(R.id.tv_title);
        mUpdateOkButton = (Button) findViewById(R.id.btn_ok);
        mNumberProgressBar = (NumberProgressBar) findViewById(R.id.npb);
        mIvClose = (ImageView) findViewById(R.id.iv_close);
    }

    private void initData() {
        mUpdateApp = (UpdateAppBean) getIntent().getSerializableExtra(UpdateAppManager.INTENT_KEY);
        if (mUpdateApp != null) {
            //弹出对话框
            String newVersion = mUpdateApp.getNew_version();
            String targetSize = mUpdateApp.getTarget_size();
            String updateLog = mUpdateApp.getUpdate_log();


            String msg = "新版本大小：" + targetSize + "\n"
                    + updateLog;


            mTitleTextView.setText(String.format("是否升级到%s版本？", newVersion));
            mContentTextView.setText(msg);
            //强制更新
            if (mUpdateApp.isConstraint()) {
                mIvClose.setVisibility(View.GONE);
            }
            initEvents();
        }
    }

    private void initEvents() {
        mUpdateOkButton.setOnClickListener(this);
        mIvClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btn_ok) {
            downloadApp();
            mUpdateOkButton.setVisibility(View.GONE);
        } else if (i == R.id.iv_close) {
            finish();
        }
    }

    /**
     * 开启后台服务下载
     */
    private void downloadApp() {
        //使用ApplicationContext延长他的生命周期
        DownloadService.bindService(getApplicationContext(), conn);
    }

    /**
     * 回调监听下载
     */
    private void startDownloadApp(DownloadService.DownloadBinder binder) {
        // 开始下载，监听下载进度，可以用对话框显示
        if (mUpdateApp != null) {

            binder.start(mUpdateApp, new DownloadService.DownloadCallback() {
                @Override
                public void onStart() {
                    if (!NewDialogActivity.this.isFinishing()) {
                        mNumberProgressBar.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onProgress(float progress) {
                    if (!NewDialogActivity.this.isFinishing()) {
                        mNumberProgressBar.setProgress((int) progress);
                    }
                }

                @Override
                public void setMax(float total) {
                    if (!NewDialogActivity.this.isFinishing()) {
                        mNumberProgressBar.setMax((int) total);
                    }
                }

                @Override
                public void onFinish() {
                    if (!NewDialogActivity.this.isFinishing()) {
                        NewDialogActivity.this.finish();
                    }
                }

                @Override
                public void onError(String msg) {
                    if (!NewDialogActivity.this.isFinishing()) {
                        NewDialogActivity.this.finish();
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        //禁用
        if (mUpdateApp != null && mUpdateApp.isConstraint()) {
//          ActManager.getInstance().finishAllActivity();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        isShow = false;
        super.onDestroy();
    }
}
