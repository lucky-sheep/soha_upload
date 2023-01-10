package com.hunliji.hlj_download.upload.compress

import android.graphics.Bitmap

/**
 * Created by zhengxiaoyong on 2017/3/4.
 */
internal object JpegTurboCompressor {

    val version: String
        get() = "libjpeg-turbo api version : $libjpegTurboVersion, libjpeg api version : $libjpegVersion"

    private val libjpegTurboVersion: Int
        external get

    private val libjpegVersion: Int
        external get

    fun compress(bitmap: Bitmap, outfile: String, quality: Int): Boolean {
        return nativeCompress(bitmap, outfile, quality, true)
    }

    init {
        System.loadLibrary("tiny")
    }

    private external fun nativeCompress(
        bitmap: Bitmap,
        outfile: String,
        quality: Int,
        optimize: Boolean
    ): Boolean

}
