
## Android 版本更新

## 目录

* [功能介绍](#功能介绍)
* [效果图与示例 apk](#效果图与示例-apk)
* [Gradle 依赖](#Gradle依赖)
* [简单使用](#简单使用)
* [详细说明](#详细说明)
* [更新日志](#更新日志)
* [License](#license)

## 功能介绍

- [x] 实现android版本更新
- [x] 对kotlin适配，调用更简单
- [x] 自定义接口协议，可以不改变现有项目的协议就能使用
- [x] 支持get,post请求
- [x] 支持进度显示，对话框进度条，和通知栏进度条展示
- [x] 支持后台下载
- [x] 支持强制更新
- [x] 支持简单主题色配置(可以自动从顶部图片提取主色)
- [x] 支持自定义对话框（可以监听下载进度）
- [x] 支持静默下载（可以设置wifi状态下）
- [x] 支持android7.0

## 效果图与示例 apk

<img src="https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/image/example_01.png?raw=true" width="1000">

<img src="https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/image/example_02.png?raw=true" width="1000">

<img src="https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/image/example_03.png?raw=true" width="1000">

<img src="https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/image/example_05.png" width="1000">

<img src="https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/image/example_06.png" width="1000">

	
[点击下载 Demo.apk](https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/apk/sample-debug.apk) 或扫描下面的二维码安装

![Demo apk文件二维](https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/image/1498810770.png)



## Gradle 依赖

**java方式引用**

```gradle
dependencies {
    compile 'com.qianwen:update-app:3.5.2'
}
```

[![Download](https://api.bintray.com/packages/qianwen/maven/update-app/images/download.svg) ](https://bintray.com/qianwen/maven/update-app/_latestVersion) [![API](https://img.shields.io/badge/API-14%2B-orange.svg?style=flat)](https://android-arsenal.com/api?level=14) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![GitHub stars](https://img.shields.io/github/stars/WVector/AppUpdate.svg?style=plastic&label=Star) ](https://github.com/WVector/AppUpdate)


**kotlin方式引用**

```gradle
dependencies {
    compile 'com.qianwen:update-app-kotlin:1.2.3'
}
```

[![Download](https://api.bintray.com/packages/qianwen/maven/update-app-kotlin/images/download.svg) ](https://bintray.com/qianwen/maven/update-app/_latestVersion) [![API](https://img.shields.io/badge/API-14%2B-orange.svg?style=flat)](https://android-arsenal.com/api?level=14) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![GitHub stars](https://img.shields.io/github/stars/WVector/AppUpdate.svg?style=plastic&label=Star) ](https://github.com/WVector/AppUpdate)


## 简单使用



1,java方式

```java
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
2,kotlin方式

```kotlin
	updateApp(mUpdateUrl, UpdateAppHttpUtil()).update()
```

## 详细说明

- [java方式](java.md)
- [kotlin方式](kotlin.md)

#### 进度条使用的是代码家的「[NumberProgressBar](https://github.com/daimajia/NumberProgressBar)」

## 更新日志

kotlin版本是依赖java版本的，所以java版本的问题kotlin自然修复


v3.5.2

	1，修复下载过程中，关闭对话框不能自动安装问题。

v3.5.1

	1，修复bug

v3.5.0

	1，优化强制更新 

v3.4.8 

	1,修复bug
	
v3.4.7 

	1,优化 APP 安装的问题
	
v3.4.6 

	1,优化 APP 安装的问题

v3.4.5

	1，增加全局异常捕获方法
	
	                .handleException(new ExceptionHandler() 						{
                    @Override
                    public void onException(Exception e) {

                    }
                })

v3.4.4

	1，修复bug
[bug](https://github.com/WVector/AppUpdate/pull/68)

v3.4.3

	1，修复bug
[bug](https://github.com/WVector/AppUpdate/pull/67)
   
v3.4.2

	1,修复bug
[bug](https://github.com/WVector/AppUpdate/pull/66)

v3.4.1

	1,给插件使用者更多的配置和开启一些钩子方便适配不同的业务需求
	2,适配android8.0

感谢[Jiiiiiin](https://github.com/Jiiiiiin)对项目的维护

v3.4.0

	1,修复  
[issues#59](https://github.com/WVector/AppUpdate/issues/59)



v3.3.9

	1,适配android8.0的通知和安装未知来源的app

感谢[typ0520](https://github.com/typ0520)对项目的维护

v3.3.8

	1,增加存储空间权限申请

V3.3.7
	
	1,修改默认安装包下载路径为download/packageName

感谢[bean-liu](https://github.com/bean-liu)对项目的维护

V3.3.6
	
	1,去掉对下载路径前缀的校验。
[https://github.com/WVector/AppUpdate/issues/26](https://github.com/WVector/AppUpdate/issues/26)

V3.3.5

	1，修复升级对话框布局中的问题。
	2，修复静默下载，关闭更新弹窗 再点击更新 一直显示的问题。
[https://github.com/WVector/AppUpdate/issues/21](https://github.com/WVector/AppUpdate/issues/21)

V3.3.4

	1，修复对话框更新内容过多，升级按钮被挤压的问题。
	2，去掉自动从图片提取颜色的功能， 通过.setThemeColor()设置按钮和精度条颜色，
	3，兼容compileSdkVersion <25

V3.3.3

	1，修复下载路径是重定向路径不能下载的问题

V3.3.2

	1，修复正在下载时，返回桌面报错的问题
[https://github.com/WVector/AppUpdate/issues/14](https://github.com/WVector/AppUpdate/issues/14)

V3.3.1

	1，修复对话框外可以点击的问题

V3.3.0 

	1，可以设置不显示通知栏进度条。
	2，可以设置忽略版本。 
	3，优化下载时页面卡的问题（由于下载进度回调调用频繁，造成ui线程阻塞）。
	4，可以静默下载，类似网易云音乐，并且设置wifi状态下。

 V3.2.9 

	1，新增自定义对话框。
	2，适配kotlin，写法更简单。 


## License

   	Copyright 2017 千匍

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.