package com.hunliji.hlj_download.upload

import android.text.TextUtils
import android.util.Log
import com.blankj.utilcode.util.GsonUtils
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hunliji.hlj_download.upload.compress.Compress
import com.hunliji.hlj_download.upload.constract.UploadConstant
import com.hunliji.hlj_download.upload.constract.UploadConstant.Companion.putThreshold
import com.hunliji.hlj_download.upload.model.*
import com.hunliji.hlj_download.upload.net.FileService
import com.hunliji.hlj_download.upload.net.ProgressBody
import com.hunliji.hlj_download.upload.net.RetrofitClient
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import java.io.*
import java.util.*

/**
 * UploadHelper
 *
 * @author wm
 * @date 20-3-2
 */
object UploadHelper : CoroutineScope by MainScope() {

    private val retrofit by lazy {
        RetrofitClient.getInstance().create(FileService::class.java)
    }

    private var parseToken: ((JsonObject) -> String)? = null
    private var globalTokenRetrofit: Retrofit? = null
    private var globalHost: String = ""
    private var uploadInfo: UploadInfo? = null
    private var globalTokenPath: String = UploadConstant.GLOBAL_TOKEN_PATH
    private var job: Job? = null
    private fun <T> request(
        block: suspend CoroutineScope.() -> T,
        build: UploadBuilder<T>.() -> Unit = {}
    ) {
        val builder = UploadBuilder<T>().also(build)
        job = launch {
            start(
                { withContext(Dispatchers.IO) { block() } },
                { response ->
                    builder.getSuccess()?.invoke(response)
                },
                {
                    builder.getError()?.invoke(it)
                }
            )
        }
    }

    private var uploadMap: WeakHashMap<String, HljUploadResult> = WeakHashMap()

    private suspend fun <T> start(
        block: suspend CoroutineScope.() -> T,
        success: suspend CoroutineScope.(T) -> Unit,
        error: suspend CoroutineScope.(Throwable) -> Unit
    ) {
        coroutineScope {
            try {
                success(block())
            } catch (e: Throwable) {
                error(e)
            }
        }
    }

    fun setUp(
        retrofit: Retrofit? = null,
        host: String = "",
        tokenPath: String = UploadConstant.GLOBAL_TOKEN_PATH,
        parseToken: ((JsonObject) -> String)? = null
    ) {
        this.parseToken = parseToken
        this.globalTokenRetrofit = retrofit
        this.globalHost = host
        this.globalTokenPath = tokenPath
    }

    fun uploadSingle(
        file: File,
        from: String = "",
        userId: Long = 0,
        type: Int = FileType.FAMILY_IMAGE.type,
        inputTokenPath: String? = null,
        init: UploadBuilder<HljUploadResult>.() -> Unit
    ) {
        val builder = UploadBuilder<HljUploadResult>().also(init)
        builder.getStart()?.invoke()
        val globalTokenService = globalTokenRetrofit?.create(FileService::class.java)
        val initToken = builder.getToken()
        request({
            val token = if (!TextUtils.isEmpty(initToken)) {
                initToken
            } else {
                val tokenPath = if (TextUtils.isEmpty(inputTokenPath)) {
                    realTokenPath(globalTokenPath, from)
                } else {
                    realTokenPath(inputTokenPath!!, from)
                }
                Log.e("test", "tokenPath:${tokenPath}")
                val tokenObject: JsonObject =
                    (globalTokenService?.getToken(tokenPath))
                        ?: throw Throwable(message = "token没取到")
                parseToken?.invoke(
                    tokenObject
                ) ?: builder.getTokenParse()?.invoke(tokenObject)
                ?: parseToken(tokenObject).fileUploadToken ?: ""
            }
            if (TextUtils.isEmpty(token)) {
                throw Throwable(message = "token没取到")
            }
            val compress = builder.getCompress()
            var upLoadFile = file
            if (compress && file.name.toLowerCase(Locale.getDefault()).endsWith(".jpg")) {
                Compress.compressImageFile(file, builder.getQuality(), File(Compress.compressPath))
                    ?.let {
                        if (it.exists() && it.length() > 0) {
                            upLoadFile = it
                        }
                    }
            }
            val fileHash = getFileHash(upLoadFile)
            val loadResult = if (upLoadFile.length() < putThreshold) {
                val fileBody: RequestBody =
                    ProgressBody(RequestBody.create(null, upLoadFile), builder.getProgress())
                val part = MultipartBody.Part.createFormData(
                    "file",
                    "${userId}_${Utils.MD5(file.name)}",
                    fileBody
                )
                val tokenBody = RequestBody.create(MediaType.parse("text/plain"), token)
                var keyBody: RequestBody? = null
                if (upLoadFile.name.toLowerCase(Locale.getDefault()).endsWith(".gif")) {
                    var name = EncodeUtil.md5sum(upLoadFile)
                    if (TextUtils.isEmpty(name)) {
                        name = DeviceUuidFactory.getInstance()
                            .getDeviceUuidString(null) + System.currentTimeMillis()
                    }
                    keyBody = RequestBody.create(MediaType.parse("text/plain"), "$name.gif")
                } else {
                    var name = EncodeUtil.md5sum(upLoadFile)
                    if (TextUtils.isEmpty(name)) {
                        name = DeviceUuidFactory.getInstance()
                            .getDeviceUuidString(null) + System.currentTimeMillis()
                    }
                    keyBody = RequestBody.create(MediaType.parse("text/plain"), "${uploadInfo?.getImageType(type)}${userId}_${name}.png")
                }
                Log.e("imageHostUrl", "=========${uploadInfo?.imageHostUrl}")
                retrofit.uploadFile(tokenBody, part, keyBody)
            } else {
                Block(
                    upLoadFile,
                    token,
                    fileHash,
                    retrofit,
                    builder.getProgress()
                ).makeBlock()
            }
            if (TextUtils.isEmpty(fileHash) || TextUtils.isEmpty(loadResult.hash) || fileHash == loadResult.hash) {
                loadResult
            } else {
                throw Throwable(message = "文件损坏上传失败")
            }
        }) {
            success {
                builder.getSuccess()?.invoke(it)
            }
            error {
                builder.getError()?.invoke(it)
            }
        }
    }

    fun uploadGroup(
        list: List<UpLoadInterface>,
        type: Int = FileType.FAMILY_IMAGE.type,
        init: UploadBuilder<List<HljUploadResult>>.() -> Unit
    ) {
        val builder = UploadBuilder<List<HljUploadResult>>().also(init)
        val index = 0
        val uploadList = mutableListOf<HljUploadResult>()
        val map = mutableMapOf<String, String>()
        builder.getStart()?.invoke()
        val globalTokenService = globalTokenRetrofit?.create(FileService::class.java)
        uploadV2(index, map,type, list, uploadList, builder, globalTokenService)
    }

    private fun requestToken(
        index: Int,
        map: MutableMap<String, String>,
        type: Int = FileType.FAMILY_IMAGE.type,
        list: List<UpLoadInterface>,
        uploadList: MutableList<HljUploadResult>,
        builder: UploadBuilder<List<HljUploadResult>>,
        globalTokenService: FileService?
    ) {
        request({
            val customTokenPath = list[index].tokenPath()
            val tokenPath = if (TextUtils.isEmpty(customTokenPath)) {
                realTokenPath(globalTokenPath, list[index].from())
            } else {
                realTokenPath(customTokenPath!!, list[index].from())
            }
            val tokenObject: JsonObject =
                (globalTokenService?.getToken(tokenPath)) ?: throw Throwable(message = "token没取到")
            val token = parseToken?.invoke(
                tokenObject
            ) ?: builder.getTokenParse()?.invoke(tokenObject) ?: parseToken(tokenObject).fileUploadToken?:""
            if (TextUtils.isEmpty(token)) {
                throw Throwable(message = "token没取到")
            }
            map[tokenPath] = token
        }) {
            success {
                uploadV2(index, map,type, list, uploadList, builder, globalTokenService)
            }
            error {
                builder.getError()?.invoke(it)
            }
        }
    }

    private fun uploadV2(
        index: Int,
        map: MutableMap<String, String>,
        type: Int = FileType.FAMILY_IMAGE.type,
        list: List<UpLoadInterface>,
        uploadList: MutableList<HljUploadResult>,
        builder: UploadBuilder<List<HljUploadResult>>,
        globalTokenService: FileService?
    ) {
        if (index >= list.size) {
            builder.getSuccess()?.invoke(uploadList)
            return
        }
        val upLoadItem = list[index]
        val filePath = upLoadItem.source()
        val from = upLoadItem.from()
        val tokenPath = upLoadItem.tokenPath()
        when {
            skipUpLoad(filePath) -> {
                uploadList.add(HljUploadResult().also {
                    it.url = filePath ?: ""
                    it.width = upLoadItem.width()
                    it.height = upLoadItem.height()
                })
                builder.getCount()?.invoke(uploadList.size)
                uploadV2(index + 1, map, type, list, uploadList, builder, globalTokenService)
            }
            else -> {
                val hljUploadResult = uploadMap["$filePath/$tokenPath/$from"]
                val acceptedToken = acceptedToken(tokenPath, from, map)
                when {
                    hljUploadResult != null -> {
                        uploadList.add(hljUploadResult)
                        builder.getCount()?.invoke(uploadList.size)
                        uploadV2(index + 1, map, type,list, uploadList, builder, globalTokenService)
                    }
                    TextUtils.isEmpty(acceptedToken) -> {
                        requestToken(index, map, type,list, uploadList, builder, globalTokenService)
                    }
                    else -> {
                        Log.e("hlj_upload", "accept获得token:${realTokenPath(globalTokenPath, from)}")
                        uploadSingle(File(filePath)) {
                            setToken(acceptedToken!!)
                            setCompress(builder.getCompress())
                            setQuality(builder.getQuality())
                            success {
                                uploadList.add(it)
                                uploadMap["$filePath/$tokenPath/$from"] = it
                                builder.getCount()?.invoke(uploadList.size)
                                uploadV2(
                                    index + 1,
                                    map,
                                    type,
                                    list,
                                    uploadList,
                                    builder,
                                    globalTokenService
                                )
                            }
                            error {
                                builder.getError()?.invoke(it)
                            }
                            progress { soFar, total ->
                                builder.getProgress()?.invoke(soFar, total)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun skipUpLoad(filePath: String?): Boolean = filePath.isHttpUrl()
            || TextUtils.isEmpty(filePath)
            || !File(filePath).exists()
            || File(filePath).length() == 0L

    private fun acceptedToken(tokenPath: String?, from: String, map: MutableMap<String, String>) =
        if (TextUtils.isEmpty(tokenPath)) {
            map[realTokenPath(globalTokenPath, from)]
        } else {
            map[realTokenPath(tokenPath!!, from)]
        }

    private fun String?.isHttpUrl(): Boolean {
        return !TextUtils.isEmpty(this) && (this?.startsWith("http://") ?: false || (this?.startsWith(
            "https://"
        ) ?: false))
    }

    private fun parseToken(jsonObject: JsonObject): UploadInfo {
        Log.e("live_download", "jsonObject:$jsonObject")
        val fromJson = GsonUtils.fromJson(jsonObject.toString(), TokenInfo::class.java)
        if (fromJson.resultCode ==10000 && fromJson.data != null){
            this.uploadInfo = fromJson.data
            Domain.doamin = this.uploadInfo?.imageHostUrl
            return fromJson.data
        }
       return UploadInfo()
    }

    private fun getFileHash(file: File): String {
        return try {
            Etag.file(file)
        } catch (e: IOException) {
            ""
        }
    }

    private fun realTokenPath(tokenPath: String, from: String): String {
        return if (TextUtils.isEmpty(from))
            tokenPath
        else
            "$tokenPath?from=$from"
    }

    fun cancel() {
        job?.cancel()
    }
}



