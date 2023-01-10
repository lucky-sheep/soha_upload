package com.hunliji.mvvm.binding

import android.annotation.SuppressLint
import androidx.databinding.BindingAdapter
import com.hunliji.toolbar_helper.ToolbarHelper

/**
 * ToolBarHelperBindAdapter
 *
 * @author wm
 * @date 20-2-29
 */
object ToolBarHelperBindAdapter {
    @SuppressLint("CheckResult")
    @JvmStatic
    @BindingAdapter("current", "total", "scrollStartColor", "scrollEndColor", requireAll = false)
    fun bindData(
        view: ToolbarHelper,
        current: Int,
        total: Int,
        scrollStartColor: String,
        scrollEndColor: String
    ) {
        view.setScroller(current,total,scrollStartColor,scrollEndColor)
    }
}
