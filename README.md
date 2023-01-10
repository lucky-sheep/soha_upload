# hlj_android_download_upload

婚礼纪安卓上传下载module

* 下载

* 依赖

```
implementation 'com.hunliji:integration-mw:1.0.1'
implementation 'com.liulishuo.filedownloader:library:1.7.7'
//上传所需
implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0'
implementation 'com.squareup.retrofit2:retrofit:2.6.0'
implementation 'com.squareup.retrofit2:converter-gson:2.6.0'
```

* 使用方式

```
allprojects {
    repositories {
        ...
        maven { url 'http://172.16.12.102:8081/artifactory/libs-release-local/' }
    }
}
```

```
implementation 'com.hunliji:hlj-download:1.0.1'
```

* 混淆

```
-keep class **.model.**{*;}
```

共分为三种下载方式

1.单独下载

```
downloadSingle(
        sourcePath: String,
        targetPath: String,
        init: DownLoadBuilder.() -> Unit
)

demo:
downloadSingle(
        sourcePath: String,
        targetPath: String
){
    start {
       下载开始
    }
    success{
        下载成功，返回targetPath
    }
    progress{
        下载进度，Float,写百分比需要自行*100
    }
    progressSoFar2Total{
        下载进度，soFar和total
    }
    error{
        下载失败，throwable
    }
    task{
        进阶用法，下面具体介绍
    }
}
```

2.并行下载

```
fun downLoadGroupTogether(
        sourcePathList: List<String>,
        targetPathList: List<String>,
        init: DownLoadBuilder.() -> Unit
)

demo:
DownHelper.downLoadGroupTogether(
        sourcePathList,
        targetPathList
) {
    start {
       下载开始
    }
    success {
       下载成功，返回targetPathList
    }
    progress{
       下载进度，Float,写百分比需要自行*100(返回的是平均进度)
    }
    error{
       下载失败，throwable
    }
    count{
       已经下载的个数，从1开始
    }
    task{
       进阶用法，下面具体介绍
    }
}
```

3.串行下载


```
fun downloadGroupSequentially(
        sourcePathList: List<String>,
        targetPathList: List<String>,
        init: DownLoadBuilder.() -> Unit
)

demo:
DownHelper.downloadGroupSequentially(
        sourcePathList,
        targetPathList
) {
    start {
       下载开始
    }
    success {
       下载成功，返回targetPathList
    }
    progress{
       下载进度，Float,每一个的单独进度回调
    }
    progressSoFar2Total{
       下载进度，soFar和total
    }
    error{
       下载失败，throwable
    }
    count{
       已经下载的个数，从1开始
    }
    task{
       进阶用法，下面具体介绍
    }
}
```

* task的使用

task中具有taskId,适用于比较细节的操作

```
例如：

DownHelper.downLoadGroupTogether(
                sourcePathList: List<String>,
                targetPathList: List<String>
            ) {
                task { task ->
                    //找到对应downUrl的task
                    list.find {
                        (it as? DownLoadItemVm)?.bean?.downUrl == task.url
                    }?.let {
                        //为显示部分赋值
                        (it as? DownLoadItemVm)?.apply {
                            //赋值taskId,主要用于单个暂停
                            taskIdLD.value = task.id
                            if (task.smallFileTotalBytes > 0) {
                                //每一个条目的进度
                                progressLD.value =
                                    (task.smallFileSoFarBytes * 100 / task.smallFileTotalBytes)
                                //每一个条目当前的下载量/总的下载量
                                bytes.value =
                                    "${task.smallFileSoFarBytes}/${task.smallFileTotalBytes}"
                            }
                        }
                    }
                }
                success{
                    Log.e("test", "success")
                }
            }
```

* 其余Api

方法 | 作用
---- | ----
pauseAll() | 全部暂停
pauseByTaskId(taskId: Int = -1) | 通过指定taskId暂停

上传

分为两种上传模式

1.单独上传

```
需配置获取token路径，见注意
UploadHelper.uploadSingle(File("abc/down0.mp4".toSdCardFilePath())){
   success{
       HljUploadResult 上传成功
   }
   progress{ soFar,total->
       上传进度
   }
   error{
       上传失败，throwable
   }
   start {
       上传开始
   }
   //是否压缩，只支持.jpg模式的图片
   setCompress()
   //压缩图片质量，默认100
   setQuality(quality: Int = 100)
}
```

2.批量上传

```
需配置获取token路径，见注意
 UploadHelper.uploadGroup(
       list
       ) {
          setCompress(true)
          start {
             上传开始
          }
          success {
             List<HljUploadResult> 上传成功
          }
          progress { soFar, total ->
             上传进度
          }
          count {
             已经上传的个数，从1开始
          }
          error {
             上传失败，throwable
          }
}
```

* 注意 token路径的配置

支持5种模式，按照优先级为

```
1. setToken(token) 直接设置token

2. 自行配置token网络请求 suspend类型
 UploadHelper.uploadGroup(
       list,{retrofit.getToken(path)}
       ) {
          setCompress(true)
          start {
             上传开始
          }
          success {
             List<HljUploadResult> 上传成功
          }
          progress { soFar, total ->
             上传进度
          }
          count {
             已经上传的个数，从1开始
          }
          error {
             上传失败，throwable
          }

3. setRetrofit(retrofit) 设置retrofit

4. setHost() 并且 setTokenPath() 可以是通过setUp设置的  设置host以及tokenPath

5. UploadHelper.setUp(retrofit: Retrofit? = null, host = "",tokenPath: String? = "",parseToken: ((JsonObject) -> String)? = null)
全局设置retrofit,只有当以上任何一个都没有配置的时候使用

配置token解析方式
同时，可以全局设置请求回的 token 解析方式
也可单独配置当前的tokenParse
setTokenParse{
   ""
}
如全局默认均未设置，则采取默认
默认为：
private fun parseToken(jsonObject: JsonObject): String {
   return if (jsonObject.has("uptoken")) {
       jsonObject.get("uptoken").asString
   } else if (jsonObject.has("token")) {
       jsonObject.get("token").asString
   } else if (jsonObject.has("data")) {
       val dataObj = jsonObject.get("data")
          .asJsonObject
       if (dataObj != null && dataObj.has("upToken")) {
          dataObj.get("upToken").asString
        } else {
           ""
        }
     } else {
            ""
    }
}

//设置命名空间
setFrom(from)
3,4,5均支持设置命名空间

```

* 其余Api

方法 | 作用
---- | ----
UploadHelper.cancel() | 取消上传









