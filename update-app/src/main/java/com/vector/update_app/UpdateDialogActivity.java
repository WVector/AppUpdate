package com.vector.update_app;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vector.update_app.service.DownloadService;
import com.vector.update_app.view.NumberProgressBar;


/**
 * Created by fengjunming_t on 2016/5/25 0025.
 */
public class UpdateDialogActivity extends FragmentActivity implements View.OnClickListener {

    private TextView mContentTextView;
    private Button mUpdateOkButton;
    private Button mUpdateCancelButton;
    private UpdateAppBean mUpdateApp;
    public static boolean isShow = false;
    private NumberProgressBar mNumberProgressBar;
    private ImageView mIvClose;
    private LinearLayout mBtnContainer;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isShow = true;
        setContentView(R.layout.update_dialog);
        initView();
        initData();
    }

    private void initData() {
        mUpdateApp = (UpdateAppBean) getIntent().getSerializableExtra(UpdateAppManager.INTENT_KEY);
        if (mUpdateApp != null) {
            //弹出对话框
            String newVersion = mUpdateApp.getNew_version();
            String targetSize = mUpdateApp.getTarget_size();
            String updateLog = mUpdateApp.getUpdate_log();


            String msg = "最新版本：" + newVersion + "\n" +
                    "新版本大小：" + targetSize + "\n\n" +
                    "更新内容：\n" + updateLog + "\n";

            mContentTextView.setText(msg);
            //强制更新
            if (mUpdateApp.isConstraint()) {
                mUpdateCancelButton.setVisibility(View.GONE);
                mIvClose.setVisibility(View.GONE);
            }
            initEvents();
        }
    }

    private void initView() {
        mContentTextView = (TextView) findViewById(R.id.umeng_update_content);
        mUpdateOkButton = (Button) findViewById(R.id.umeng_update_id_ok);
        mUpdateCancelButton = (Button) findViewById(R.id.umeng_update_id_cancel);
        mNumberProgressBar = (NumberProgressBar) findViewById(R.id.numberbar1);
        mIvClose = (ImageView) findViewById(R.id.umeng_update_id_close);
        mBtnContainer = (LinearLayout) findViewById(R.id.ll_btns);
    }

    private void initEvents() {
        mUpdateCancelButton.setOnClickListener(this);
        mUpdateOkButton.setOnClickListener(this);
        mIvClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.umeng_update_id_ok) {
            downloadApp();
            mBtnContainer.setVisibility(View.GONE);

            if (mUpdateApp.isConstraint()) {
                mUpdateOkButton.setEnabled(false);
            } else {
//                    finish();
            }

        } else if (i == R.id.umeng_update_id_cancel) {
            finish();

        } else if (i == R.id.umeng_update_id_close) {
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

    /**
     * 回调监听下载
     */
    private void startDownloadApp(DownloadService.DownloadBinder binder) {
        // 开始下载，监听下载进度，可以用对话框显示
        if (mUpdateApp != null) {

            binder.start(mUpdateApp, new DownloadService.DownloadCallback() {
                @Override
                public void onStart() {
                    if (!UpdateDialogActivity.this.isFinishing()) {
                        mNumberProgressBar.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onProgress(float progress) {
                    if (!UpdateDialogActivity.this.isFinishing()) {
                        mNumberProgressBar.setProgress((int) progress);
                    }
                }

                @Override
                public void setMax(float total) {
                    if (!UpdateDialogActivity.this.isFinishing()) {
                        mNumberProgressBar.setMax((int) total);
                    }
                }

                @Override
                public void onFinish() {
                    if (!UpdateDialogActivity.this.isFinishing()) {
                        UpdateDialogActivity.this.finish();
                    }
                }

                @Override
                public void onError(String msg) {
                    if (!UpdateDialogActivity.this.isFinishing()) {
                        UpdateDialogActivity.this.finish();
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
            int myPid = Process.myPid();
            android.os.Process.killProcess(myPid);
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        isShow = false;
        super.onDestroy();
    }
}
