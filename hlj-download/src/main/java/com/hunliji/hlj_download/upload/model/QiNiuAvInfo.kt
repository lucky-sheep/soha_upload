package com.hunliji.hlj_download.upload.model

import com.google.gson.annotations.SerializedName

/**
 * Created by wangtao on 2017/8/3.
 */

class QiNiuAvInfo {

    @SerializedName("video")
    val videoInfo: QiNiuVideoInfo? = null

    inner class QiNiuVideoInfo {
        @SerializedName("duration")
        var duration: Float = 0.toFloat()
            internal set
        @SerializedName("width")
        var width: Int = 0
            internal set
        @SerializedName("height")
        var height: Int = 0
            internal set
    }
}
