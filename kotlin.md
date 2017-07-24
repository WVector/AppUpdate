
### 目录

* [默认接口协议](#默认接口协议)
* [自定义接口协议](#自定义接口协议)
* [自定义接口协议+自定义对话框](#自定义接口协议+自定义对话框)
* [自定义接口协议+自定义对话框+显示进度对话框](#自定义接口协议+自定义对话框+显示进度对话框)
* [静默下载](#静默下载)
* [静默下载+自定义对话框](#静默下载+自定义对话框)
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

	updateApp(mUpdateUrl, UpdateAppHttpUtil()).update()
   	            
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
            hideDialogOnDownloading(true)
            //设置头部，不设置显示默认的图片，设置图片后自动识别主色调，然后为按钮，进度条设置颜色
            topPic = R.mipmap.top_8
            //为按钮，进度条设置颜色，默认从顶部图片自动识别。
			//setThemeColor(ColorUtil.getRandomColor())
            //设置apk下砸路径，默认是在下载到sd卡下/Download/1.0.0/test.apk
            targetPath = path
            //设置appKey，默认从AndroidManifest.xml获取，如果，使用自定义参数，则此项无效
			//setAppKey("ab55ce55Ac4bcP408cPb8c1Aaeac179c5f6f")

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


```

### 自定义接口协议+自定义对话框

其他代码和上面一样，只需重写UpdateCallback 的 hasNewApp方法，然后调用自己的对话框

```java

	 hasNewApp { updateApp, updateAppManager ->
                        showDiyDialog(updateApp, updateAppManager)
                    }

```



下面是简单的对话框，新版本信息从  updateApp 对象获取，updateAppManager 可以控制后台开始下载，下载完自动安装

直接调用 'updateAppManager.download();' ，进行下载。

```java

       dialog("是否升级到${updateApp.newVersion}版本？"
                , "新版本大小：${updateApp.targetSize}\n\n${updateApp.updateLog}")
        {
            positiveButton("升级") {
                updateAppManager.download()
                dismiss()

            }
            negativeButton("暂不升级") {
                dismiss()
            }
            show()
        }

```

### 自定义接口协议+自定义对话框+显示进度对话框

和上面的例子只有在控制下载有区别，传个回调，监听到下载进度。

onFinish() 当返回 true ：下载完自动跳到安装界面，false：则不进行安装

```java

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
```

### 静默下载
以下是使用默认协议的例子，
```java


	  updateApp(mUpdateUrl, UpdateAppHttpUtil())
	        {
	            setOnlyWifi()
	        }.silenceUpdate()



```

### 静默下载+自定义对话框

以下是使用默认协议的例子，也可以使用自定义协议（请参考自定义协议例子）

```java


       updateApp(mUpdateUrl, UpdateAppHttpUtil())
        {
            setOnlyWifi()
        }.checkNewApp(object : SilenceUpdateCallback() {
            override fun showDialog(updateApp: UpdateAppBean, updateAppManager: UpdateAppManager?, appFile: File) {
                showSilenceDiyDialog(updateApp, appFile)
            }
        })


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

