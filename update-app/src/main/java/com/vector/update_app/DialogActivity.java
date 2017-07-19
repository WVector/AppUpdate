package com.vector.update_app;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vector.update_app.service.DownloadService;
import com.vector.update_app.utils.ColorUtil;
import com.vector.update_app.utils.DrawableUtil;
import com.vector.update_app.utils.Utils;
import com.vector.update_app.view.NumberProgressBar;

/**
 * 新版本提交对话框
 */
public class DialogActivity extends FragmentActivity implements View.OnClickListener {


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
    private LinearLayout mLlClose;
    //默认色
    private int mDefaultColor = 0xffe94339;
    private int mDefaultPicResId = R.mipmap.lib_update_app_top_bg;
    private ImageView mTopIv;
    private TextView mIgnore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isShow = true;
        setContentView(R.layout.lib_update_app_dialog);
        initView();
        initData();
    }

    private void initView() {
        //提示内容
        mContentTextView = (TextView) findViewById(R.id.tv_update_info);
        //标题
        mTitleTextView = (TextView) findViewById(R.id.tv_title);
        //更新按钮
        mUpdateOkButton = (Button) findViewById(R.id.btn_ok);
        //进度条
        mNumberProgressBar = (NumberProgressBar) findViewById(R.id.npb);
        //关闭按钮
        mIvClose = (ImageView) findViewById(R.id.iv_close);
        //关闭按钮+线 的整个布局
        mLlClose = (LinearLayout) findViewById(R.id.ll_close);
        //顶部图片
        mTopIv = (ImageView) findViewById(R.id.iv_top);
        //忽略
        mIgnore = (TextView) findViewById(R.id.tv_ignore);
    }

    private void initData() {
        mUpdateApp = (UpdateAppBean) getIntent().getSerializableExtra(UpdateAppManager.INTENT_KEY);
        //设置主题色
        initTheme();


        if (mUpdateApp != null) {
            //弹出对话框
            String newVersion = mUpdateApp.getNewVersion();
            String targetSize = mUpdateApp.getTargetSize();
            String updateLog = mUpdateApp.getUpdateLog();

            String msg = "";

            if (!TextUtils.isEmpty(targetSize)) {
                msg = "新版本大小：" + targetSize + "\n\n";
            }

            if (!TextUtils.isEmpty(updateLog)) {
                msg += updateLog;
            }

            //更新内容
            mContentTextView.setText(msg);
            //标题
            mTitleTextView.setText(String.format("是否升级到%s版本？", newVersion));
            //强制更新
            if (mUpdateApp.isConstraint()) {
                mLlClose.setVisibility(View.GONE);
            } else {
                //不是强制更新时，才生效
                if (mUpdateApp.isShowIgnoreVersion()) {
                    mIgnore.setVisibility(View.VISIBLE);
                }
            }

            initEvents();
        }
    }

    /**
     * 初始化主题色
     */
    private void initTheme() {


        final int color = getIntent().getIntExtra(UpdateAppManager.THEME_KEY, -1);

        final int topResId = getIntent().getIntExtra(UpdateAppManager.TOP_IMAGE_KEY, -1);


        if (-1 == topResId) {
            if (-1 == color) {
                //默认红色
                setDialogTheme(mDefaultColor, mDefaultPicResId);
            } else {
                setDialogTheme(color, mDefaultPicResId);
            }

        } else {
            if (-1 == color) {
                //自动提色
                Palette.from(Utils.drawableToBitmap(this.getResources().getDrawable(topResId))).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        int mDominantColor = palette.getDominantColor(mDefaultColor);
                        setDialogTheme(mDominantColor, topResId);
                    }
                });
            } else {
                //更加指定的上色
                setDialogTheme(color, topResId);
            }
        }


    }

    /**
     * 设置
     *
     * @param color    主色
     * @param topResId 图片
     */
    private void setDialogTheme(int color, int topResId) {
        mTopIv.setImageResource(topResId);
        mUpdateOkButton.setBackgroundDrawable(DrawableUtil.getDrawable(Utils.dip2px(4, this), color));
        mNumberProgressBar.setProgressTextColor(color);
        mNumberProgressBar.setReachedBarColor(color);
        //随背景颜色变化
        mUpdateOkButton.setTextColor(ColorUtil.isTextColorDark(color) ? Color.BLACK : Color.WHITE);
    }

    private void initEvents() {
        mUpdateOkButton.setOnClickListener(this);
        mIvClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btn_ok) {
            installApp();
            mUpdateOkButton.setVisibility(View.GONE);
        } else if (i == R.id.iv_close) {
//            if (mNumberProgressBar.getVisibility() == View.VISIBLE) {
//                Toast.makeText(getApplicationContext(), "后台更新app", Toast.LENGTH_LONG).show();
//            }
            onBackPressed();
        } else if (i == R.id.tv_ignore) {
            Utils.saveIgnoreVersion(this, mUpdateApp.getNewVersion());
            onBackPressed();
        }
    }

    private void installApp() {
        if (Utils.appIsDownloaded(mUpdateApp)) {
            Utils.installApp(this, Utils.getAppFile(mUpdateApp));
            //安装完自杀
            onBackPressed();
        } else {
            downloadApp();
            if (mUpdateApp.isHideDialog()) {
                onBackPressed();
            }

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
                    if (!DialogActivity.this.isFinishing()) {
                        mNumberProgressBar.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onProgress(float progress, long totalSize) {
                    if (!DialogActivity.this.isFinishing()) {
                        mNumberProgressBar.setProgress(Math.round(progress * 100));
                        mNumberProgressBar.setMax(100);
                    }
                }

                @Override
                public void setMax(long total) {

                }

                @Override
                public void onFinish() {
                    if (!DialogActivity.this.isFinishing()) {
                        DialogActivity.this.finish();
                    }
                }

                @Override
                public void onError(String msg) {
                    if (!DialogActivity.this.isFinishing()) {
                        DialogActivity.this.finish();
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
//            android.os.Process.killProcess(android.os.Process.myPid());
            //返回桌面
            startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
            return;
        }
        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        isShow = false;
        super.onDestroy();
    }
}
