package com.hunliji.hlj_download.upload.model

import com.google.gson.JsonObject
import retrofit2.Retrofit

/**
 * UploadBuilder
 *
 * @author wm
 * @date 20-3-2
 */
class UploadBuilder<T> {
    private var success: ((T) -> Unit)? = null
    private var error: ((Throwable) -> Unit)? = null
    private var progress: ((soFar: Long, total: Long) -> Unit)? = null
    private var start: (() -> Unit)? = null
    private var compress = false
    private var quality = 100
    private var count: ((Int) -> Unit)? = null
    private var host = ""
    private var tokenPath = ""
    private var retrofit: Retrofit? = null
    private var token: String = ""
    private var tokenParse: ((JsonObject) -> String)? = null

    fun getTokenParse(): ((JsonObject) -> String)? {
        return tokenParse
    }

    fun setTokenParse(method: ((JsonObject) -> String)? = null) {
        this.tokenParse = method
    }

    fun setToken(token: String) {
        this.token = token
    }

    fun getToken(): String {
        return token
    }

    fun setRetrofit(retrofit: Retrofit?) {
        this.retrofit = retrofit
    }

    fun getRetrofit(): Retrofit? {
        return retrofit
    }

    fun setTokenPath(tokenPath: String) {
        this.tokenPath = tokenPath
    }

    fun getTokenPath(): String {
        return tokenPath
    }

    fun setHost(host: String) {
        this.host = host
    }

    fun getHost(): String {
        return host
    }

    fun count(method: (Int) -> Unit = {}) {
        count = method
    }

    fun getCount(): ((Int) -> Unit)? {
        return count
    }

    fun setQuality(quality: Int = 100) {
        this.quality = quality
    }

    fun getQuality(): Int {
        return quality
    }

    fun setCompress(compress: Boolean = false) {
        this.compress = compress
    }

    fun getCompress(): Boolean {
        return compress
    }

    fun start(method: () -> Unit) {
        start = method
    }

    fun getStart(): (() -> Unit)? {
        return start
    }

    fun success(method: (T) -> Unit) {
        success = method
    }

    fun getSuccess(): ((T) -> Unit)? {
        return success
    }

    fun error(method: (Throwable) -> Unit) {
        error = method
    }

    fun getError(): ((Throwable) -> Unit)? {
        return error
    }

    fun progress(method: (soFar: Long, total: Long) -> Unit) {
        progress = method
    }

    fun getProgress(): ((Long, Long) -> Unit)? {
        return progress
    }
}