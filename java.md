
### 目录

* [默认接口协议](#默认接口协议)
* [自定义接口协议](#自定义接口协议)
* [自定义接口协议+自定义对话框](#自定义接口协议+自定义对话框)
* [自定义接口协议+自定义对话框+显示进度对话框](#自定义接口协议+自定义对话框+显示进度对话框)
* [静默下载](#静默下载)
* [静默下载+自定义对话框](#静默下载+自定义对话框)
* [只用下载功能](#只用下载功能)
* [实现HttpManager接口](#实现HttpManager接口)

### 默认接口协议 

#### 1，Request 请求参数

GET：[https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/json/json.txt?appKey=ab55ce55Ac4bcP408cPb8c1Aaeac179c5f6f&version=0.1.0](https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/json/json.txt?appKey=ab55ce55Ac4bcP408cPb8c1Aaeac179c5f6f&version=0.1.0)



	1,参数 appkey 
	app的唯一标志
	appkey可以在manifest文件中配置，也可以在代码中添加
	xml配置如下：

```xml

  <meta-data
            android:name="UPDATE_APP_KEY"
            android:value="ab55ce55Ac4bcP408cPb8c1Aaeac179c5f6f"/>

```
	
	2,参数 version 
	版本号，工具自动添加(服务器判断客户端传过来的version和服务器存的最新的version，决定是否更新)
	
```json

```

	3, 服务器app后台管理界面 

[点击下载后台代码](https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/web/AppVersionManger.rar) 

<img src="https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/image/example_04.png?raw=true" width="1000">

#### 2, Response 服务器的返回json格式
	1,有新版本

```json

{
  "update": "Yes",//有新版本
  "new_version": "0.8.3",//新版本号
  "apk_file_url": "https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/apk/app-debug.apk", //apk下载地址
  "update_log": "1，添加删除信用卡接口\r\n2，添加vip认证\r\n3，区分自定义消费，一个小时不限制。\r\n4，添加放弃任务接口，小时内不生成。\r\n5，消费任务手动生成。",//更新内容
  "target_size": "5M",//apk大小
  "new_md5":"A818AD325EACC199BC62C552A32C35F2",
  "constraint": false//是否强制更新
}

```

	2,没有新版本

```json

{
  "update": "No",//没有新版本
}

```


#### 3,客户端检测是否有新版本，并且更新下载

和自定义相比，不需要传自定义参数，和实现parseJson方法，其他都一样。


```java

	//最简方式
	new UpdateAppManager
                .Builder()
                //当前Activity
                .setActivity(this)
                //更新地址
                .setUpdateUrl(mUpdateUrl)
                //实现httpManager接口的对象
                .setHttpManager(new UpdateAppHttpUtil())
                .build()
                .update();
	
   	            
```

### 自定义接口协议

根据自己项目的接口，自己传参数给服务器，实现parseJson方法，解析json，设置新版本app信息。

同时可以设置以下功能

- 请求方式，get,post
- 请求参数
- 是否显示下载进度对话框
- 对话框顶部图片（设置图片后自动识别主色调，然后为按钮，进度条设置颜色）
- 按钮，进度条颜色
- apk的下载路径
- 是否忽略版本
- 是否显示通知栏进度条

如果以下的例子出错，请看项目中详细的使用案例

```java

 		String path = Environment.getExternalStorageDirectory().getAbsolutePath();

        Map<String, String> params = new HashMap<String, String>();

        params.put("appKey", "ab55ce55Ac4bcP408cPb8c1Aaeac179c5f6f");
        params.put("appVersion", Utils.getVersionName(this));
        params.put("key1", "value2");
        params.put("key2", "value3");

        new UpdateAppManager
                .Builder()
                //必须设置，当前Activity
                .setActivity(this)
                //必须设置，实现httpManager接口的对象
                .setHttpManager(new OkGoUpdateHttpUtil())
                //必须设置，更新地址
                .setUpdateUrl(mUpdateUrl)

                //以下设置，都是可选
                //设置请求方式，默认get
                .setPost(false)
                //添加自定义参数，默认version=1.0.0（app的versionName）；apkKey=唯一表示（在AndroidManifest.xml配置）
                .setParams(params)
                //设置点击升级后，消失对话框，默认点击升级后，对话框显示下载进度
                .hideDialogOnDownloading(false)
                //设置头部，不设置显示默认的图片，设置图片后自动识别主色调，然后为按钮，进度条设置颜色
                .setTopPic(R.mipmap.top_8)
                //为按钮，进度条设置颜色，默认从顶部图片自动识别。
                //.setThemeColor(ColorUtil.getRandomColor())
                //设置apk下砸路径，默认是在下载到sd卡下/Download/1.0.0/test.apk
                .setTargetPath(path)
                //设置appKey，默认从AndroidManifest.xml获取，如果，使用自定义参数，则此项无效
                //.setAppKey("ab55ce55Ac4bcP408cPb8c1Aaeac179c5f6f")
                //不显示通知栏进度条
                .dismissNotificationProgress()
                //是否忽略版本
				//.showIgnoreVersion()

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
                                    .setNewMd5(jsonObject.optString("new_md51"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return updateAppBean;
                    }

                    /**
                     * 网络请求之前
                     */
                    @Override
                    public void onBefore() {
                        CProgressDialogUtils.showProgressDialog(JavaActivity.this);
                    }

                    /**
                     * 网路请求之后
                     */
                    @Override
                    public void onAfter() {
                        CProgressDialogUtils.cancelProgressDialog(JavaActivity.this);
                    }

                    /**
                     * 没有新版本
                     */
                    @Override
                    public void noNewApp() {
                        Toast.makeText(JavaActivity.this, "没有新版本", Toast.LENGTH_SHORT).show();
                    }
                });



```

### 自定义接口协议+自定义对话框

其他代码和上面一样，只需重写UpdateCallback 的 hasNewApp方法，然后调用自己的对话框

```java

	/**
     * 有新版本
     *
     * @param updateApp        新版本信息
     * @param updateAppManager app更新管理器
     */
    @Override
    public void hasNewApp(UpdateAppBean updateApp, UpdateAppManager updateAppManager) {
        //自定义对话框
        showDiyDialog(updateApp, updateAppManager);
    }

```



下面是简单的对话框，新版本信息从  updateApp 对象获取，updateAppManager 可以控制后台开始下载，下载完自动安装

直接调用 'updateAppManager.download();' ，进行下载。

```java

    /**
     * 自定义对话框
     *
     * @param updateApp
     * @param updateAppManager
     */
    private void showDiyDialog(final UpdateAppBean updateApp, final UpdateAppManager updateAppManager) {
        String targetSize = updateApp.getTargetSize();
        String updateLog = updateApp.getUpdateLog();

        String msg = "";

        if (!TextUtils.isEmpty(targetSize)) {
            msg = "新版本大小：" + targetSize + "\n\n";
        }

        if (!TextUtils.isEmpty(updateLog)) {
            msg += updateLog;
        }

        new AlertDialog.Builder(this)
                .setTitle(String.format("是否升级到%s版本？", updateApp.getNewVersion()))
                .setMessage(msg)
                .setPositiveButton("升级", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            //不显示下载进度
                            updateAppManager.download();

                        dialog.dismiss();
                    }
                })
                .setNegativeButton("暂不升级", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

```

### 自定义接口协议+自定义对话框+显示进度对话框

和上面的例子只有在控制下载有区别，传个回调，监听到下载进度。

onFinish() 当返回 true ：下载完自动跳到安装界面，false：则不进行安装

```java

 	updateAppManager.download(new DownloadService.DownloadCallback() {
                                @Override
                                public void onStart() {
                                    HProgressDialogUtils.showHorizontalProgressDialog(JavaActivity.this, "下载进度", false);
                                }

                                /**
                                 * 进度
                                 *
                                 * @param progress  进度 0.00 -1.00 ，总大小
                                 * @param totalSize 总大小 单位B
                                 */
                                @Override
                                public void onProgress(float progress, long totalSize) {
                                    HProgressDialogUtils.setProgress(Math.round(progress * 100));
                                }

                                /**
                                 *
                                 * @param total 总大小 单位B
                                 */
                                @Override
                                public void setMax(long total) {

                                }

						 		/**
						         * 下载完了
						         * @param file 下载的app
						         * @return true ：下载完自动跳到安装界面，false：则不进行安装
						         */
                                @Override
                                public boolean onFinish(File file) {
                                    HProgressDialogUtils.cancel();
                                    return true;
                                }

                                @Override
                                public void onError(String msg) {
                                    Toast.makeText(JavaActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    HProgressDialogUtils.cancel();

                                }
                            });

```

### 静默下载
以下是使用默认协议的例子，
```java

	/**
     * 静默下载，下载完才弹出升级界面
     *
     * @param view
     */
    public void silenceUpdateApp(View view) {
        new UpdateAppManager
                .Builder()
                //当前Activity
                .setActivity(this)
                //更新地址
                .setUpdateUrl(mUpdateUrl)
                //实现httpManager接口的对象
                .setHttpManager(new UpdateAppHttpUtil())
                //只有wifi下进行，静默下载(只对静默下载有效)
                .setOnlyWifi()
                .build()
                .silenceUpdate();
    }


```

### 静默下载+自定义对话框

以下是使用默认协议的例子，也可以使用自定义协议（请参考自定义协议例子）

```java

 	/**
     * 静默下载，并且自定义对话框
     *
     * @param view
     */
    public void silenceUpdateAppAndDiyDialog(View view) {
        new UpdateAppManager
                .Builder()
                //当前Activity
                .setActivity(this)
                //更新地址
                .setUpdateUrl(mUpdateUrl)
                //实现httpManager接口的对象
                .setHttpManager(new UpdateAppHttpUtil())
                //只有wifi下进行，静默下载(只对静默下载有效)
                .setOnlyWifi()
                .build()
                .checkNewApp(new SilenceUpdateCallback() {
                    @Override
                    protected void showDialog(UpdateAppBean updateApp, UpdateAppManager updateAppManager, File appFile) {
                        showSilenceDiyDialog(updateApp, appFile);
                    }
                });
    }


  	/**
     * 静默下载自定义对话框
     *
     * @param updateApp
     * @param appFile
     */
    private void showSilenceDiyDialog(final UpdateAppBean updateApp, final File appFile) {
        String targetSize = updateApp.getTargetSize();
        String updateLog = updateApp.getUpdateLog();

        String msg = "";

        if (!TextUtils.isEmpty(targetSize)) {
            msg = "新版本大小：" + targetSize + "\n\n";
        }

        if (!TextUtils.isEmpty(updateLog)) {
            msg += updateLog;
        }

        new AlertDialog.Builder(this)
                .setTitle(String.format("是否升级到%s版本？", updateApp.getNewVersion()))
                .setMessage(msg)
                .setPositiveButton("安装", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppUpdateUtils.installApp(JavaActivity.this, appFile);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("暂不升级", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

```
### 只用下载功能

```java

        UpdateAppBean updateAppBean = new UpdateAppBean();

        //设置 apk 的下载地址
        updateAppBean.setApkFileUrl("https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/apk/app-debug.apk");

        String path = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable()) {
            try {
                path = getExternalCacheDir().getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(path)) {
                path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
            }
        } else {
            path = getCacheDir().getAbsolutePath();
        }

        //设置apk 的保存路径
        updateAppBean.setTargetPath(path);
        //实现网络接口，只实现下载就可以
        updateAppBean.setHttpManager(new UpdateAppHttpUtil());

        UpdateAppManager.download(this, updateAppBean, new DownloadService.DownloadCallback() {
            @Override
            public void onStart() {
                HProgressDialogUtils.showHorizontalProgressDialog(JavaActivity.this, "下载进度", false);
                Log.d(TAG, "onStart() called");
            }

            @Override
            public void onProgress(float progress, long totalSize) {
                HProgressDialogUtils.setProgress(Math.round(progress * 100));
                Log.d(TAG, "onProgress() called with: progress = [" + progress + "], totalSize = [" + totalSize + "]");

            }

            @Override
            public void setMax(long totalSize) {
                Log.d(TAG, "setMax() called with: totalSize = [" + totalSize + "]");
            }

            @Override
            public boolean onFinish(File file) {
                HProgressDialogUtils.cancel();
                Log.d(TAG, "onFinish() called with: file = [" + file.getAbsolutePath() + "]");
                return true;
            }

            @Override
            public void onError(String msg) {
                HProgressDialogUtils.cancel();
                Log.e(TAG, "onError() called with: msg = [" + msg + "]");
            }

            @Override
            public boolean onInstallAppAndAppOnForeground(File file) {
                Log.d(TAG, "onInstallAppAndAppOnForeground() called with: file = [" + file + "]");
                return false;
            }
        });

```

### 实现HttpManager接口

根据自己项目使用的网络框架，自己实现HttpManager接口

```java

	class UpdateAppHttpUtil implements HttpManager {
	    /**
	     * 异步get
	     *
	     * @param url      get请求地址
	     * @param params   get参数
	     * @param callBack 回调
	     */
	    @Override
	    public void asyncGet(@NonNull String url, @NonNull Map<String, String> params, @NonNull final Callback callBack) {
	        OkHttpUtils.get()
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
	
	    /**
	     * 异步post
	     *
	     * @param url      post请求地址
	     * @param params   post请求参数
	     * @param callBack 回调
	     */
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
	
	    /**
	     * 下载
	     *
	     * @param url      下载地址
	     * @param path     文件保存路径
	     * @param fileName 文件名称
	     * @param callback 回调
	     */
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

```

