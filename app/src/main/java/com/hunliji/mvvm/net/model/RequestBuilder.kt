package com.hunliji.mvvm.net.model

import com.hunliji.mvvm.net.exception.LoadingType

/**
 * RequestBuilder
 *
 * @author wm
 * @date 20-2-25
 */
class RequestBuilder<T> {
    private var success: ((T) -> Unit)? = null
    private var error: ((Throwable) -> Unit)? = null
    private var normal = true
    private var refresh = true
    private var loadingType: LoadingType = LoadingType.STATUS

    fun setNormal(normal:Boolean){
        this.normal = normal
    }

    fun getNormal():Boolean{
        return normal
    }

    fun setRefresh(refresh:Boolean){
        this.refresh = refresh
    }

    fun getRefresh():Boolean{
        return refresh
    }

    fun setLoadingType(type: LoadingType){
        this.loadingType = type
    }

    fun getLoadingType(): LoadingType {
        return loadingType
    }

    fun success(method: (T) -> Unit = {}) {
        success = method
    }

    fun getSuccess(): ((T) -> Unit)? {
        return success
    }

    fun error(method: (Throwable) -> Unit = {}) {
        error = method
    }

    fun getError(): ((Throwable) -> Unit)? {
        return error
    }
}
