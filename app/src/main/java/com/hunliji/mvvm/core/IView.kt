package com.hunliji.mvvm.core

import androidx.annotation.LayoutRes

/**
 * IView
 *
 * @author wm
 * @date 19-8-13
 */
interface IView {
    @LayoutRes
    fun getLayoutId(): Int

    fun initView()
}
