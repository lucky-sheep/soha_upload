package com.hunliji.mvvm.loading

import android.content.Context
import android.view.View
import com.hunliji.mvvm.R
import com.kingja.loadsir.callback.Callback

/**
 * 空布局
 *
 * @author wm
 * @date 19-7-1
 */
class DefaultEmptyCallback : Callback() {
    override fun getSuccessVisible(): Boolean {
        return false
    }

    override fun onCreateView(): Int {
        return R.layout.common_view_empty
    }

    override fun onReloadEvent(context: Context?, view: View?): Boolean {
        return false
    }
}
