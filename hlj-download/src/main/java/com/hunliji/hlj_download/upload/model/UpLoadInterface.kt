package com.hunliji.hlj_download.upload.model


/**
 * UpLoadInterface
 *
 * @author kou_zhong
 * @date 2020/10/9
 */
interface UpLoadInterface {

    fun source(): String?

    fun from(): String

    fun tokenPath(): String? = null

    fun width(): Int = 0

    fun height(): Int = 0

}