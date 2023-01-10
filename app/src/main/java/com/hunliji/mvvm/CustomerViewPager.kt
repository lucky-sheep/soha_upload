package com.hunliji.mvvm

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

import androidx.viewpager.widget.ViewPager

/**
 * CustomerViewPager
 *
 * @author wm
 * @date 19-9-2
 */
class CustomerViewPager : ViewPager {
    private var noScroll = false

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context) : super(context)

    fun setNoScroll(noScroll: Boolean) {
        this.noScroll = noScroll
    }

    override fun scrollTo(x: Int, y: Int) {
        super.scrollTo(x, y)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(arg0: MotionEvent): Boolean {
        /* return false;//super.onTouchEvent(arg0); */
        when (arg0.action) {

        }
        return !noScroll && super.onTouchEvent(arg0)
    }

    override fun onInterceptTouchEvent(arg0: MotionEvent): Boolean {
        return !noScroll && super.onInterceptTouchEvent(arg0)
    }

    override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        super.setCurrentItem(item, smoothScroll)
    }

    override fun setCurrentItem(item: Int) {
        //false 去除滚动效果
        super.setCurrentItem(item, false)
    }
}
