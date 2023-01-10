package com.hunliji.mvvm.net.api

/**
 *   @auther : Aleyn
 *   time   : 2020/01/13
 */
interface IBaseResponse<T> {
    fun code(): Int
    fun msg(): String
    fun data(): T
    fun isSuccess(): Boolean
}