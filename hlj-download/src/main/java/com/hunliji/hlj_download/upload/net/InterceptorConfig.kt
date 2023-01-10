package com.hunliji.hlj_download.upload.net

import android.util.Log
import okhttp3.Interceptor

/**
 * 拦截器配置
 *
 * @author wm
 * @date 19-8-8
 */
internal object InterceptorConfig {
    private const val TAG = "retrofit_net"

    /**
     * log拦截器
     */
    val LoggingInterceptor: Interceptor by lazy {
        Interceptor { chain ->
            val request = chain.request()
            val t1 = System.nanoTime()
            Log.e(
                TAG, String.format("Sending requestSample %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()))
            try {
                val response = chain.proceed(request)
                val t2 = System.nanoTime()
                val body = response.peekBody(1024 * 1024)
                val content = body.string()
                Log.i(
                    TAG, String.format("接收响应: [%s] %n返回json:【%s】 %.1fms %n%s",
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
}
