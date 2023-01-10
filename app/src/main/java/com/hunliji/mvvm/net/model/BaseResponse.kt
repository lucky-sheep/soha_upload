package com.hunliji.mvvm.net.model

import com.google.gson.annotations.SerializedName

/**
 * BaseResponse
 *
 * @author wm
 * @date 19-8-11
 */
data class BaseResponse<out T>(
    val status: Status,
    val data: T?,
    @SerializedName(value = "current_time")
    val currentTime: Long = 0
) {
    fun isSuccess() = status.retCode == 0
}

data class Status(
    @SerializedName(value = "RetCode", alternate = ["code", "retCode"])
    val retCode: Int,
    val msg: String,
    @SerializedName(value = "current_time")
    val currentTime: Long = 0
)


