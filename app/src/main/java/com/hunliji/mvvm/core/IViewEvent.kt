package com.hunliji.mvvm.core

import android.view.View

/**
 * View事件处理
 *
 * @author wm
 * @date 19-8-27
 */
interface IViewEvent : View.OnClickListener {
    override fun onClick(v: View?)
}
