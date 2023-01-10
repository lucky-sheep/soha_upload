package com.hunliji.hlj_download.upload

import android.text.TextUtils
import android.util.Base64
import com.hunliji.hlj_download.upload.constract.UploadConstant.Companion.BLOCK_SIZE
import com.hunliji.hlj_download.upload.constract.UploadConstant.Companion.CHUNK_SIZE
import com.hunliji.hlj_download.upload.model.BlockUploadResult
import com.hunliji.hlj_download.upload.model.HljUploadResult
import com.hunliji.hlj_download.upload.net.FileService
import com.hunliji.hlj_download.upload.net.ProgressBody
import com.hunliji.hlj_download.upload.net.UploadListener
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.io.RandomAccessFile
import java.io.UnsupportedEncodingException
import java.util.*
import java.util.zip.CRC32

/**
 * Block
 *
 * @author wm
 * @date 20-3-2
 */
internal class Block(
    val file: File,
    token: String,
    private val fileHash: String,
    private val fileService: FileService,
    val progress: ((Long, Long) -> Unit)? = null
) : UploadListener {
    private var randomAccessFile: RandomAccessFile
    private var offset: Long = 0
    private var totalSize: Long = 0
    private var headers = mutableMapOf<String, String>()
    private var uploadContexts = mutableListOf<String>()
    private var key: String
    private var result: HljUploadResult? = null
    private var current = 0L

    init {
        key = getKey()
        totalSize = file.length()
        headers["Authorization"] = "UpToken $token"
        randomAccessFile = RandomAccessFile(file, "r")
    }

    suspend fun makeBlock(): HljUploadResult {
        val chunk = chunkBuffer
        val offset = offset
        val body = if (chunk.isNotEmpty()) {
            val t = MediaType.parse("application/octet-stream")
            RequestBody.create(t, chunk, 0, chunk.size)
        } else {
            RequestBody.create(null, ByteArray(0))
        }
        val requestBody: RequestBody = ProgressBody(body, null, totalSize, this)
        val blockSize = toBytes(chunk)
        val makeBlock =
            fileService.makeBlock(calcBlockSize(offset), requestBody, headers)
        if (TextUtils.isEmpty(makeBlock.uploadContext) || makeBlock.crc32 != blockSize) {
            throw Throwable(message = "上传失败")
        } else {
            upLoadBlockOrChunk(makeBlock)
            return result ?: throw Throwable(message = "上传失败null")
        }
    }

    private suspend fun upLoadBlockOrChunk(makeBlock: BlockUploadResult) {
        val offsetLocal = offset
        offset += calcPutSize(offsetLocal)
        when {
            offset == totalSize -> {
                uploadContexts.add(makeBlock.uploadContext)
                val stringBuilder = StringBuilder()
                for (uploadContext in uploadContexts) {
                    if (stringBuilder.isNotEmpty()) {
                        stringBuilder.append(",")
                    }
                    stringBuilder.append(uploadContext)
                }
                val totalBody = RequestBody.create(
                    MediaType.parse("text/plain"),
                    stringBuilder.toString()
                )
                result = if (TextUtils.isEmpty(key)) {
                    fileService.makeFile(totalSize, totalBody, headers)
                } else {
                    fileService.makeFile(totalSize, key, totalBody, headers)
                }
            }
            offset % BLOCK_SIZE == 0L -> {
                uploadContexts.add(makeBlock.uploadContext)
                makeBlock()
            }
            else -> {
                chunkUpload(makeBlock)
            }
        }
    }

    private suspend fun chunkUpload(makeBlock: BlockUploadResult) {
        val chunk = chunkBuffer
        val body = if (chunk.isNotEmpty()) {
            val t = MediaType.parse("application/octet-stream")
            RequestBody.create(t, chunk, 0, chunk.size)
        } else {
            RequestBody.create(null, ByteArray(0))
        }
        val requestBody: RequestBody = ProgressBody(body, null, totalSize, this)
        val blockSize = toBytes(chunk)
        val uploadChunk = fileService.uploadChunk(
            makeBlock.uploadContext,
            offset % BLOCK_SIZE,
            requestBody,
            headers
        )
        if (TextUtils.isEmpty(makeBlock.uploadContext) || uploadChunk.crc32 != blockSize) {
            throw Throwable(message = "上传失败")
        } else {
            upLoadBlockOrChunk(uploadChunk)
        }
    }

    private fun toBytes(chunk: ByteArray): Long {
        val crc32 = CRC32()
        crc32.update(chunk, 0, chunk.size)
        return crc32.value
    }

    private val chunkBuffer: ByteArray
        get() {
            val offsetLocal = offset
            val chunkSize = calcPutSize(offsetLocal).toInt()
            val chunkBuffer = ByteArray(chunkSize)
            randomAccessFile.seek(offset)
            randomAccessFile.read(chunkBuffer, 0, chunkSize)
            return chunkBuffer
        }

    private fun calcPutSize(offset: Long): Long {
        val left = totalSize - offset
        return if (left < CHUNK_SIZE) left else CHUNK_SIZE
    }

    private fun calcBlockSize(offset: Long): Long {
        val left = totalSize - offset
        return if (left < BLOCK_SIZE) left else BLOCK_SIZE
    }

    private fun getKey(): String {
        var key = ""
        if (file.name.toLowerCase(Locale.getDefault()).endsWith(".gif")) {
            if (!TextUtils.isEmpty(fileHash)) {
                key = "$fileHash.gif"
            } else {
                var name = EncodeUtil.md5sum(file)
                if (TextUtils.isEmpty(name)) {
                    name = DeviceUuidFactory.getInstance()
                        .getDeviceUuidString(null) + System.currentTimeMillis()
                }
                key = "$name.gif"
                try {
                    key = Base64.encodeToString(
                        key.toByteArray(charset("utf-8")),
                        Base64.URL_SAFE or Base64.NO_WRAP
                    )
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
            }
        }
        return key
    }

    override fun transferred(soFar: Long) {
        current += soFar
        if (current > totalSize) {
            current = totalSize
        }
        progress?.invoke(current, totalSize)
    }
}
