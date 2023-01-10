package com.hunliji.hlj_download.upload.constract

/**
 * UploadConstant
 *
 * @author wm
 * @date 20-3-3
 */
internal class UploadConstant {
    companion object {
        const val BLOCK_SIZE = 4.toLong() * 1024 * 1024
        const val CHUNK_SIZE = 256.toLong() * 1024
        const val putThreshold = 4 * 1024 * 1024
        const val GLOBAL_TOKEN_PATH = "api/sdk/qiniuToken"
    }
}
