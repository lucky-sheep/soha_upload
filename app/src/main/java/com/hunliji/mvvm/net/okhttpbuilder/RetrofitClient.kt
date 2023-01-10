package com.hunliji.mvvm.net.okhttpbuilder

import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * NetConvert
 *
 * @author wm
 * @date 20-2-25
 */
class RetrofitClient {
    val retrofit1: Retrofit
    var retrofit: Retrofit
    companion object {
        fun getInstance() = SingletonHolder.INSTANCE
    }

    private object SingletonHolder {
        val INSTANCE = RetrofitClient()
    }

    init {
        retrofit = Retrofit.Builder()
            .client(getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://www.wanandroid.com/")
            .build()

        retrofit1 = Retrofit.Builder()
            .client(getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://dev-api.hunliji.com")
            .build()
    }

    fun get():Retrofit{
        return retrofit1
    }

    private fun getOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(20L, TimeUnit.SECONDS)
            .writeTimeout(20L, TimeUnit.SECONDS)
            .addInterceptor(InterceptorConfig.LoggingInterceptor)
            .connectionPool(ConnectionPool(8, 15, TimeUnit.SECONDS))
            .build()
    }

    fun <T> create(service: Class<T>?): T =
        retrofit.create(service!!) ?: throw RuntimeException("Api service is null!")
}