package com.hunliji.hlj_download.upload.net

import android.os.Handler
import android.os.Looper
import android.os.Message
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.Buffer
import okio.BufferedSink
import okio.ForwardingSink
import okio.Okio
import java.io.IOException
import java.lang.ref.WeakReference

/**
 * ProgressBody
 *
 * @author wm
 * @date 20-3-3
 */
internal class ProgressBody(
    private val requestBody: RequestBody,
    private val progress: ((Long, Long) -> Unit)? = null,
    totalSize: Long = 0L,
    private val listener: UploadListener? = null
) :
    RequestBody() {

    private var contentLength = 0L
    private var bytesWritten = 0L

    companion object {
        const val WHAT = 1
    }

    private val weakHandler by lazy {
        WeakReference<Handler>(object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message?) {
                msg?.let {
                    when (msg.what) {
                        WHAT -> {
                            progress?.invoke(bytesWritten, contentLength)
                            listener?.transferred(bytesWritten)
                        }
                        else -> {

                        }
                    }
                }
            }
        })
    }

    init {
        try {
            contentLength = if (totalSize == 0L) {
                requestBody.contentLength()
            } else {
                totalSize
            }
            bytesWritten = 0
            weakHandler.get()?.obtainMessage(WHAT)?.sendToTarget()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun contentType(): MediaType? {
        return requestBody.contentType()
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return requestBody.contentLength()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        val bufferedSink = Okio.buffer(object : ForwardingSink(sink) {
            @Throws(IOException::class)
            override fun write(source: Buffer, byteCount: Long) {
                super.write(source, byteCount)
                bytesWritten += byteCount
                weakHandler.get()?.obtainMessage(WHAT)?.sendToTarget()
            }
        })
        requestBody.writeTo(bufferedSink)
        bufferedSink.flush()
    }
}
