# AppUpdateDemo
## App更新工具
1，自己实现网络工具
  我使用okhhtp

	class UpdateAppHttpUtil implements HttpManager {
	    @Override
	    public void asyncPost(@NonNull String url, @NonNull Map<String, String> params, @NonNull final Callback callBack) {
	        OkHttpUtils.post()
	                .url(url)
	                .params(params)
	                .build()
	                .execute(new StringCallback() {
	                    @Override
	                    public void onError(Call call, Response response, Exception e, int id) {
	                        callBack.onError(validateError(e, response));
	                    }
	
	                    @Override
	                    public void onResponse(String response, int id) {
	                        callBack.onResponse(response);
	                    }
	                });
	
	    }
	
	    @Override
	    public void download(@NonNull String url, @NonNull String path, @NonNull String fileName, @NonNull final FileCallback callback) {
	        OkHttpUtils.get()
	                .url(url)
	                .build()
	                .execute(new FileCallBack(path, fileName) {
	                    @Override
	                    public void inProgress(float progress, long total, int id) {
	                        super.inProgress(progress, total, id);
	                        callback.onProgress(progress, total);
	                    }
	
	                    @Override
	                    public void onError(Call call, Response response, Exception e, int id) {
	                        callback.onError(validateError(e, response));
	                    }
	
	                    @Override
	                    public void onResponse(File response, int id) {
	                        callback.onResponse(response);
	
	                    }
	
	                    @Override
	                    public void onBefore(Request request, int id) {
	                        super.onBefore(request, id);
	                        callback.onBefore();
	                    }
	                });
	
	    }
	}

2, 客户端和服务器的json格式，服务按照以下格式传输就可以

	{
	    "update": "Yes",
	    "new_version": "0.7.0",
	    "apk_file_url": "http://**********/app-0.7.0-2017-06-22-release.apk",
	    "update_log": "1，优化启动页\r\n2，对app进行加固",
	    "target_size": "5M",
	    "constraint": false,
	    "status": "success",
	    "msg": "ok",
	    "timestamp": 1498210305442
	}


3,客户端检测是否有新版本，并且更新下载

        String url = "http://***********/com/mobile/updateVersion.html";
        String appKey = "ab55ce55Ac4bcP408cPb8c1Aaeac179c5f6f";

        final String targetPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        final UpdateAppHttpUtil httpManager = new UpdateAppHttpUtil();

        final UpdateAppManager updateAppManager = UpdateAppManager.getInstance();

        updateAppManager.updateApp(this, httpManager, url, appKey, new UpdateCallback() {
            @Override
            public void hasNewApp(@Nullable UpdateAppBean updateApp) {
                if (updateApp.isConstraint()) {
                    //强制更新
                } else {
                    //正常更新
                }
                updateAppManager.showUpdatedDialog(httpManager, MainActivity.this, targetPath, updateApp);
            }

            @Override
            public void onAfter() {
                Log.d(TAG, "onAfter() called");
                CProgressDialogUtils.cancelProgressDialog(MainActivity.this);
            }

            @Override
            public void noNewApp() {
                Toast.makeText(MainActivity.this, "没有新版本", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBefore() {
                Log.d(TAG, "onBefore() called");
                CProgressDialogUtils.showProgressDialog(MainActivity.this);
            }

        });