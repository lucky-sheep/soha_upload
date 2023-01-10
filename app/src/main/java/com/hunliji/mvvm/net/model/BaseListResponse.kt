package com.hunliji.mvvm.net.model

import com.google.gson.annotations.SerializedName

/**
 * BaseListResponse
 *
 * @author wm
 * @date 19-8-11
 */
data class BaseListResponse<out T>(
    @SerializedName(value = "total_count", alternate = ["total", "totalCount"])
    val totalCount: Int = 0,
    @SerializedName(value = "page_count", alternate = ["pageCount", "totalPage", "total_page"])
    val pageCount: Int = 0,
    @SerializedName(value = "hasNext")
    val hasNext: Boolean = false,
    @SerializedName(value = "list", alternate = ["data"])
    val data: List<T> = mutableListOf(),
    val page: Int = 0
)
