
## App更新工具

## 目录

* [功能介绍](#功能介绍)
* [效果图与示例 apk](#效果图与示例-apk)
* [使用](#使用)
* [License](#license)

## 功能介绍

- [x] 实现app版本更新
- [x] 支持get,post请求
- [x] 支持进度显示，对话框进度，和通知栏进度条展示
- [x] 支持简单主题色配置
- [x] 完美支持android7.0

## 效果图与示例 apk

<img src="https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/image/example_01.png?raw=true" width="1000">
	
[点击下载 Demo.apk](https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/apk/app-debug.apk) 或扫描下面的二维码安装

![Demo apk文件二维](https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/image/1498810770.png)

## 使用 

### Gradle 依赖

```gradle
dependencies {
    compile 'com.qianwen:update-app:3.0.0'
}
```

[![Download](https://api.bintray.com/packages/qianwen/maven/update-app/images/download.svg) ](https://bintray.com/qianwen/maven/update-app/_latestVersion) [![API](https://img.shields.io/badge/API-14%2B-orange.svg?style=flat)](https://android-arsenal.com/api?level=14) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

### 接口说明

#### 1，和服务器交互请求参数
	1,appkey app的唯一标志
	appkey可以再manifest文件中配置，可以在代码中添加
	xml配置如下：
```xml
  <meta-data
            android:name="UPDATE_APP_KEY"
            android:value="ab55ce55Ac4bcP408cPb8c1Aaeac179c5f6f"/>
```
	
	2,version 版本号，工具自动添加

#### 2, 服务器的返回json格式
	1,有新版本

```json
{
  "update": "Yes",
  "new_version": "0.8.3",
  "apk_file_url": "https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/apk/app-debug.apk",
  "update_log": "1，添加删除信用卡接口\r\n2，添加vip认证\r\n3，区分自定义消费，一个小时不限制。\r\n4，添加放弃任务接口，小时内不生成。\r\n5，消费任务手动生成。",
  "target_size": "5M",
  "constraint": false,
  "status": "success",
  "msg": "ok",
  "timestamp": 1498785412690
}
```

	2,没有新版本

```json
{
  "update": "No",
  "status": "success",
  "msg": "ok",
  "timestamp": 1498785412690
}
```
### 1，根据自己项目使用的网络框架，自己实现HttpManager接口，

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



### 3,客户端检测是否有新版本，并且更新下载

```java

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
	             * @param updateApp 新版本信息
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

   	            
```

## License

    Copyright 2015 bingoogolapple

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.