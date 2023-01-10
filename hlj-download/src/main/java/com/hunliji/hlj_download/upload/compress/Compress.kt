package com.hunliji.hlj_download.upload.compress

import android.content.ContentResolver
import android.content.Context
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import com.hunliji.hlj_download.core.app
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Compress
 *
 * @author wm
 * @date 20-3-3
 */
internal object Compress {
    fun compressImageFile(
        file: File, quality: Int, out: File
    ): File? {
        var bitmap = compressDecodeBitmap(app, file)
            ?: return null
        bitmap = rotateImage(
            bitmap,
            getOrientation(file.absolutePath)
        ) ?: return null
        bitmap = removeAlphaAndSetSaturation(
            bitmap,
            1.05f
        )
        val finalQuality =
            compressQuality(bitmap, quality)
        val isSuccess = compress(
            app.contentResolver,
            bitmap,
            out,
            finalQuality
        )
        return if (isSuccess) {
            out
        } else null
    }

    val compressPath: String
        get() {
            val dirPath = "${app.cacheDir.absolutePath}/upload_compress"
            val dir = File(dirPath)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            return File(dir.absolutePath, "temp_$timeStamp.jpg").absolutePath
        }

    private fun compressQuality(bitmap: Bitmap, quality: Int): Int {
        var options = 100
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos)
        if (baos.toByteArray().size / 1024 > 100) {
            var limitQuality = 85
            if (quality > 0) {
                limitQuality = quality
            }
            while (baos.toByteArray().size / 1024 > 300 && options > limitQuality) {
                baos.reset()
                options -= 5
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos)
            }
        }
        try {
            baos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return options
    }

    private fun compress(cr: ContentResolver?, bitmap: Bitmap, out: File, quality: Int): Boolean {
        var isSuccess = false
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            try {
                isSuccess = JpegTurboCompressor.compress(
                    bitmap,
                    out.absolutePath,
                    quality
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        if (!isSuccess) {
            if (cr != null) {
                try {
                    bitmap.compress(
                        Bitmap.CompressFormat.JPEG,
                        quality,
                        cr.openOutputStream(Uri.fromFile(out))
                    )
                    isSuccess = true
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }

            }
        }
        return isSuccess
    }

    private fun getOrientation(path: String): Int {
        try {
            val exifInterface = ExifInterface(path)
            val orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> return 90
                ExifInterface.ORIENTATION_ROTATE_180 -> return 180
                ExifInterface.ORIENTATION_ROTATE_270 -> return 270
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return 0
    }

    private fun rotateImage(bitmap: Bitmap?, degree: Int): Bitmap? {
        if (bitmap == null) {
            return null
        }
        if (degree > 0) {
            val matrix = Matrix()
            matrix.postRotate(degree.toFloat())
            try {
                val rotatedBitmap = Bitmap.createBitmap(
                    bitmap,
                    0,
                    0,
                    bitmap.width,
                    bitmap.height,
                    matrix,
                    true
                )
                bitmap.recycle()
                return rotatedBitmap
            } catch (ignored: OutOfMemoryError) {
            }

        }
        return bitmap
    }

    private fun removeAlphaAndSetSaturation(bitmap: Bitmap, saturation: Float): Bitmap {
        val newBitmap = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(newBitmap)
        if (bitmap.hasAlpha()) {
            canvas.drawColor(Color.WHITE)
        }
        val paint = Paint(Paint.FILTER_BITMAP_FLAG)
        val saturationMatrix = ColorMatrix()
        saturationMatrix.setSaturation(saturation)
        paint.colorFilter = ColorMatrixColorFilter(saturationMatrix)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        bitmap.recycle()
        return newBitmap
    }

    private fun compressDecodeBitmap(context: Context, file: File): Bitmap? {
        var bitmap: Bitmap? =
            null
        val opts = BitmapFactory.Options()
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888
        opts.inJustDecodeBounds = true
        decodeFile(file.absolutePath, opts)
        var rate = (opts.outHeight / 1620).coerceAtLeast(opts.outWidth / 1080)
        rate = rate.coerceAtLeast(1)
        opts.inJustDecodeBounds = false
        opts.inSampleSize = rate
        val cr = context.contentResolver
        try {
            if (cr != null) {
                var pfd: ParcelFileDescriptor? = null
                try {
                    pfd = cr.openFileDescriptor(Uri.fromFile(file), "r")
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }

                if (pfd != null) {
                    bitmap = BitmapFactory.decodeFileDescriptor(
                        pfd.fileDescriptor, null,
                        opts
                    )
                    try {
                        pfd.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                } else {
                    bitmap = decodeFile(
                        file.absolutePath,
                        opts
                    )
                }
            } else {
                bitmap = decodeFile(
                    file.absolutePath,
                    opts
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return bitmap
    }

    @JvmOverloads
    fun decodeFile(path: String, opts: BitmapFactory.Options? = null): Bitmap? {
        var bitmap: Bitmap? =
            null
        var stream: InputStream? = null
        try {
            stream = FileInputStream(path)
            bitmap = BitmapFactory.decodeStream(BufferedInputStream(stream), null, opts)
        } catch (e: Exception) {
            Log.e("BitmapFactory", "Unable to decode stream: $e")
        } finally {
            if (stream != null) {
                try {
                    stream.close()
                } catch (e: IOException) {
                    // do nothing here
                }

            }
        }
        return bitmap
    }
}
