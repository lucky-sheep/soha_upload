package com.hunliji.mvvm.net.okhttpbuilder

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request

/**
 * 拦截器配置
 *
 * @author wm
 * @date 19-8-8
 */
object InterceptorConfig {
    private const val TAG = "retrofit_net"

    /**
     * 缓存时长
     */
    private const val MAX_TIME = 60 * 60 * 24

    /**
     * log拦截器
     */
    val LoggingInterceptor: Interceptor by lazy {
        Interceptor { chain ->
            val request = chain.request()
            val t1 = System.nanoTime()
            Log.e(TAG, String.format("Sending requestSample %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()))
//            printQueryAndBody(requestSample)
            try {
                val response = chain.proceed(request)
                val t2 = System.nanoTime()
                val body = response.peekBody(1024 * 1024)
                val content = body.string()
                Log.i(TAG, String.format("接收响应: [%s] %n返回json:【%s】 %.1fms %n%s",
                        response.request().url(),
                        content,
                        (t2 - t1) / 1e6,
                        response.headers()
                ))
                response
            } catch (e: Exception) {
                chain.proceed(request)
            }
        }
    }

//    private fun printQueryAndBody(req: Request) {
//            val reqBody = req.body() ?: return
//            val url = req.url().toString()
//            val headers = req.headers().toString()
//            val buffer = Buffer()
//            reqBody.writeTo(buffer)
//            val body = buffer.clone().readString(Charsets.UTF_8)
//            buffer.close()
//            Log.i("RetrofitRequest", "Http Request: $url")
//            Log.i("RetrofitHeaders", "Headers: $headers")
//            val form: Map<String, String?> = try {
//                ConvertConfig.gson.fromJson(body, object : TypeToken<Map<String, String?>>() {}.type)
//            } catch (e: Exception) {
//                emptyMap()
//            }
//            for ((key, value) in form) {
//                if (value.isNullOrEmpty()) {
//                    continue
//                }
//                Log.i("RetrofitBody", "$key : $value")
//            }
//    }

    /**
     * 缓存拦截器
     */
//    val CacheInterceptor: Interceptor by lazy {
//        Interceptor { chain ->
//            var requestSample = chain.requestSample()
//            if (!OverSea.getContext().isNetworkAvailable()) {
//                requestSample = requestSample.newBuilder()
//                        .cacheControl(CacheControl.FORCE_CACHE)
//                        .build()
//            }
//            try {
//                val originalResponse = chain.proceed(requestSample)
//                if ((OverSea.getContext().isNetworkAvailable())) {
//                    originalResponse.newBuilder()
//                            .header("Cache-Control", "public, max-age=" + 0)
//                            .removeHeader("Pragma")
//                            .build()
//                } else {
//                    originalResponse.newBuilder()
//                            .header("Cache-Control", "public, only-if-cached, max-stale=$MAX_TIME")
//                            .removeHeader("Pragma")
//                            .build()
//                }
//            } catch (e: Exception) {
//                null
//            }
//        }
//    }

    /**
     * 端口号重定向
     */
    val HostInterceptor: Interceptor by lazy {
        Interceptor { chain ->
            val request = chain.request()
            val url = request.url()
            val scheme = url.scheme()
            val path = url.encodedPath()
            val query = url.encodedQuery()
            val sb = StringBuilder()
            val newUrl: String = sb
                    .append(scheme)
                    .append("")
                    .append(path)
                    .append("?")
                    .append(query)
                    .toString()
            val builder: Request.Builder = request.newBuilder().url(newUrl)
            chain.proceed(builder.build())
        }
    }

//    /**
//     * 请求头拦截器
//     */
//    val HeaderInterceptor: Interceptor by lazy {
//        Interceptor { chain ->
//            val requestSample = chain.requestSample()
//            val builder = requestSample.newBuilder()
//            try {
//                val authMap: Map<String, String> = HeaderMap.getHeaderMap()
//                if (authMap.isNotEmpty()) {
//                    val iterator = authMap.iterator()
//                    while (iterator.hasNext()) {
//                        val next = iterator.next()
//                        val key = next.key
//                        val value = next.value
//                        builder.header(key, value)
//                    }
//                }
//                chain.proceed(builder.build())
//            } catch (e: Exception) {
//                chain.proceed(requestSample)
//            }
//        }
//    }
}
