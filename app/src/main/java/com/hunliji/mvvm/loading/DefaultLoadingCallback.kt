package com.hunliji.mvvm.loading

import android.content.Context
import android.view.View
import com.hunliji.mvvm.R
import com.kingja.loadsir.callback.Callback

/**
 * loading状态时候的布局
 *
 * @author wm
 * @date 19-7-1
 */
class DefaultLoadingCallback(private val showWhenLoading:Boolean = false) : Callback() {
    override fun getSuccessVisible(): Boolean {
        return showWhenLoading
    }

    override fun onCreateView(): Int {
        return R.layout.common_view_loading
    }

    override fun onReloadEvent(context: Context?, view: View?): Boolean {
        return true
    }
}
