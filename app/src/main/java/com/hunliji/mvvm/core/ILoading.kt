package com.hunliji.mvvm.core

/**
 * ILoading
 *
 * @author wm
 * @date 19-8-26
 */
interface ILoading {
    fun isSupportLoading(): Boolean = true
    fun isSupportReload():Boolean = true
    fun showWhenLoading():Boolean = false
    fun onRequestReload() {

    }

    fun showLoading()

    fun hideLoading()

    fun showEmpty()

    fun showError()
}
