package com.hunliji.hlj_download.upload.net

/**
 * UploadListener
 *
 * @author wm
 * @date 20-3-2
 */
internal interface UploadListener {
    fun transferred(soFar: Long)
}
