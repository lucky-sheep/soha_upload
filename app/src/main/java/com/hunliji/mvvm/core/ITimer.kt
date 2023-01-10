package com.hunliji.mvvm.core

/**
 * ITimer
 *
 * @author wm
 * @date 19-9-5
 */
interface ITimer{
    fun isSupportTimer():Boolean = false
    fun timerDelay():Long = 2L
    fun timerInterval():Long = 1L
    fun onTimer(any:Any?=null)
}
