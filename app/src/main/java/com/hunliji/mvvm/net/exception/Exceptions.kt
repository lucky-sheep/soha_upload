package com.hunliji.mvvm.net.exception


sealed class Errors(msg: String = "") : Exception(msg) {

    object EmptyException : Errors()
    class ErrorException(val code: Int = -100, msg: String = "") : Errors(msg)
    class CustomerException(val code: Int = -100, msg: String = "") : Errors(msg)
}
