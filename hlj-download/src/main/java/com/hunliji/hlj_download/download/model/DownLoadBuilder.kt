package com.hunliji.hlj_download.download.model

import com.liulishuo.filedownloader.BaseDownloadTask

/**
 * DownLoadBuilder
 *
 * @author wm
 * @date 20-3-2
 */
class DownLoadBuilder<T> {
    private var success: ((T) -> Unit)? = null

    private var error: ((Throwable) -> Unit)? = null

    private var progress: ((Float) -> Unit)? = null

    private var count: ((Int) -> Unit)? = null

    private var start: (() -> Unit)? = null

    private var task: ((BaseDownloadTask) -> Unit)? = null

    private var progressSoFar2Total: ((soFar: Int, total: Int) -> Unit)? = null

    fun progressSoFar2Total(method: (soFar: Int, total: Int) -> Unit) {
        progressSoFar2Total = method
    }

    fun getProgressSoFar2Total(): ((soFar: Int, total: Int) -> Unit)? {
        return progressSoFar2Total
    }

    fun task(method: (BaseDownloadTask) -> Unit) {
        task = method
    }

    fun getTask(): ((BaseDownloadTask) -> Unit)? {
        return task
    }

    fun start(method: () -> Unit) {
        start = method
    }

    fun getStart(): (() -> Unit)? {
        return start
    }

    fun count(method: (Int) -> Unit = {}) {
        count = method
    }

    fun getCount(): ((Int) -> Unit)? {
        return count
    }

    fun progress(method: (Float) -> Unit = {}) {
        progress = method
    }

    fun getProgress(): ((Float) -> Unit)? {
        return progress
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
