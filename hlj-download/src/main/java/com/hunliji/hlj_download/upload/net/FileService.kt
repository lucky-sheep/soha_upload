package com.hunliji.hlj_download.upload.net

import com.google.gson.JsonObject
import com.hunliji.hlj_download.upload.model.BlockUploadResult
import com.hunliji.hlj_download.upload.model.HljUploadResult
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * FileService
 *
 * @author wm
 * @date 20-3-3
 */
internal interface FileService {

    @POST
    suspend fun getToken(@Url url: String): JsonObject

    @POST("http://up.qiniu.com")
    @Multipart
    suspend fun uploadFile(
        @Part("token") token: RequestBody,
        @Part filePart: MultipartBody.Part,
        @Part("key") key: RequestBody? = null
    ): HljUploadResult

    @POST("http://up.qiniu.com/mkblk/{blockSize}")
    suspend fun makeBlock(
        @Path("blockSize") blockSize: Long,
        @Body body: RequestBody,
        @HeaderMap headers: Map<String, String>
    ): BlockUploadResult

    @POST("http://up.qiniu.com/bput/{uploadContext}/{chunkOffset}")
    suspend fun uploadChunk(
        @Path("uploadContext") uploadContext: String,
        @Path("chunkOffset") chunkOffset: Long,
        @Body body: RequestBody,
        @HeaderMap headers: Map<String, String>
    ): BlockUploadResult

    @POST("http://up.qiniu.com/mkfile/{fileSize}/key/{encodedKey}")
    suspend fun makeFile(
        @Path("fileSize") fileSize: Long,
        @Path("encodedKey") key: String,
        @Body body: RequestBody,
        @HeaderMap headers: Map<String, String>
    ): HljUploadResult

    @POST("http://up.qiniu.com/mkfile/{fileSize}")
    suspend fun makeFile(
        @Path("fileSize") fileSize: Long,
        @Body body: RequestBody,
        @HeaderMap headers: Map<String, String>
    ): HljUploadResult
}
