package com.hunliji.mvvm

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hunliji.hlj_refresh.annotation.RefreshType
import com.hunliji.mvvm.net.api.BaseResult
import com.hunliji.mvvm.net.exception.*
import com.hunliji.mvvm.net.model.RequestBuilder
import com.hunliji.mvvm.net.exception.convertList
import kotlinx.coroutines.*

/**
 * BaseVm
 *
 * @author wm
 * @date 19-8-12
 */
open class BaseVm(app: Application, map: MutableMap<String, @JvmSuppressWildcards Any>) :
    ViewModel() {
    val stateModel = MutableLiveData<Int>()

    private fun launch(block: suspend CoroutineScope.() -> Unit) = viewModelScope.launch { block() }

    fun <T> requestMix(
        block: suspend CoroutineScope.() -> T,
        build: RequestBuilder<T>.() -> Unit = {}
    ) {
        val builder = RequestBuilder<T>().also(build)
        if (builder.getNormal()) {
            when (builder.getLoadingType()) {
                LoadingType.STATUS, LoadingType.SIMPLE -> {
                    showLoading()
                }
                else -> {

                }
            }
        }
        launch {
            startMix(
                { withContext(Dispatchers.IO) { block() } },
                { response ->
                    builder.getSuccess()?.invoke(response)
                },
                {
                    builder.getError()?.invoke(it)
                },
                builder.getRefresh(),
                builder.getNormal(),
                builder.getLoadingType()
            )
        }
    }

    private suspend fun <T> startMix(
        block: suspend CoroutineScope.() -> T,
        success: suspend CoroutineScope.(T) -> Unit,
        error: suspend CoroutineScope.(Throwable) -> Unit,
        isRefresh: Boolean = true,
        isNormal: Boolean = true,
        loadingType: LoadingType = LoadingType.STATUS
    ) {
        coroutineScope {
            try {
                success(block())
                if (isNormal) {
                    hideLoading()
                }
            } catch (e: Throwable) {
                ExceptionHandler
                    .onError(isNormal, isRefresh, loadingType, this@BaseVm, e)
                error(e)
            }
        }
    }

    fun <T> requestSingle(
        block: suspend CoroutineScope.() -> BaseResult<T>,
        build: RequestBuilder<T>.() -> Unit = {}
    ) {
        val builder = RequestBuilder<T>().also(build)
        if (builder.getNormal()) {
            when (builder.getLoadingType()) {
                LoadingType.STATUS, LoadingType.SIMPLE -> {
                    showLoading()
                }
                else -> {

                }
            }
        }
        launch {
            startSingle(
                { withContext(Dispatchers.IO) { block() } },
                { response ->
                    val convert =
                        response.convertList(builder.getRefresh(), this@BaseVm)
                    builder.getSuccess()?.invoke(convert)
                },
                {
                    builder.getError()?.invoke(it)
                },
                builder.getRefresh(),
                builder.getNormal(),
                builder.getLoadingType()
            )
        }
    }

    private suspend fun <T> startSingle(
        block: suspend CoroutineScope.() -> BaseResult<T>,
        success: suspend CoroutineScope.(BaseResult<T>) -> Unit,
        error: suspend CoroutineScope.(Throwable) -> Unit,
        isRefresh: Boolean = true,
        isNormal: Boolean = true,
        loadingType: LoadingType = LoadingType.STATUS
    ) {
        coroutineScope {
            try {
                success(block())
                if (isNormal) {
                    hideLoading()
                }
            } catch (e: Throwable) {
                ExceptionHandler
                    .onError(isNormal, isRefresh, loadingType, this@BaseVm, e)
                error(e)
            }
        }
    }

    companion object {
        const val NORMAL = -1  //正常
        const val PROGRESS = -2//显示进度条
        const val EMPTY = 11111 //列表数据为空
        const val ERROR = 22222  //网络未连接
    }

    @SuppressLint("SupportAnnotationUsage")
    @RefreshType
    val listState = MutableLiveData<Int>()

    init {
        stateModel.value = NORMAL
        listState.value = RefreshType.REFRESH_FINISH
    }

    fun showLoading() {
        stateModel.postValue(PROGRESS)
    }

    fun hideLoading() {
        stateModel.postValue(NORMAL)
    }

    fun showEmpty() {
        stateModel.postValue(EMPTY)
    }

    fun showError() {
        stateModel.postValue(ERROR)
    }
}

