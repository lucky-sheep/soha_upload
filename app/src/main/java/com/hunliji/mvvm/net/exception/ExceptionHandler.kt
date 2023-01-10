package com.hunliji.mvvm.net.exception

import android.net.ParseException
import android.util.Log
import com.google.gson.JsonParseException
import com.google.gson.stream.MalformedJsonException
import com.hunliji.hlj_refresh.annotation.RefreshType
import com.hunliji.mvvm.BaseVm
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException

/**
 * ExceptionHandler
 *
 * @author wm
 * @date 20-2-25
 */
object ExceptionHandler {

    fun onError(
        isNormal: Boolean = true,
        isRefresh: Boolean = true,
        loadingType: LoadingType = LoadingType.STATUS,
        vm: BaseVm,
        exception: Throwable
    ) {
        vm.run {
            if (isNormal) {
                when (loadingType) {
                    LoadingType.STATUS -> {
                        if (exception is Errors.EmptyException) {
                            showEmpty()
                        } else {
                            showError()
                        }
                    }
                    LoadingType.N0, LoadingType.SIMPLE -> {
                        hideLoading()
                    }
                }
            } else {
                if (exception is Errors.EmptyException) {
                    if (isRefresh) {
                        listState.postValue(RefreshType.REFRESH_NOT_MORE)
                    } else {
                        listState.postValue(RefreshType.LOAD_NOT_MORE)
                    }
                } else {
                    if (isRefresh) {
                        listState.postValue(RefreshType.REFRESH_FINISH)
                    } else {
                        listState.postValue(RefreshType.LOAD_MORE_FINISH)
                    }
                }
            }
            if (exception is Errors.ErrorException) {
                Log.e("net_error", "code:${exception.code};msg:${exception.message}")
            } else {
                val error: Throwable
                if (exception is HttpException) {
                    error = Errors.ErrorException(1000, "协议出错")
                } else if (exception is JsonParseException
                    || exception is JSONException
                    || exception is ParseException
                    || exception is MalformedJsonException
                ) {
                    error = Errors.ErrorException(1001, "解析错误")
                } else if (exception is ConnectException) {
                    error = Errors.ErrorException(1002, "网络错误")
                } else if (exception is javax.net.ssl.SSLException) {
                    error = Errors.ErrorException(1003, "证书出错")
                } else if (exception is java.net.SocketTimeoutException
                    || exception is java.net.UnknownHostException
                ) {
                    error = Errors.ErrorException(1004, "连接超时")
                } else {
                    error = Errors.ErrorException(1005, "未知错误")
                }
                Log.e("net_error", "code:${error.code};msg:${error.message}")
            }
        }
    }
}
