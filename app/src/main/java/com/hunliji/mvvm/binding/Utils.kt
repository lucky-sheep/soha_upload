package com.hunliji.mvvm.binding

import android.content.Context
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

/**
 * Utils
 *
 * @author wm
 * @date 20-2-14
 */
internal object Utils {
    fun isHttpUrl(url: String? = null): Boolean {
        return !TextUtils.isEmpty(url) && (url?.startsWith("http://") ?: false || (url?.startsWith("https://")
            ?: false))
    }

    fun dp2px(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }

    fun dp2px(context: Context, dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        )
    }

    private var signOr: String? = null

    val orSignURLEncoder: String?
        get() {
            if (TextUtils.isEmpty(signOr)) {
                try {
                    signOr = URLEncoder.encode("|", "UTF-8")
                } catch (e: UnsupportedEncodingException) {
                    signOr = "%7C"
                    e.printStackTrace()
                }
            }
            return signOr
        }

    fun widthAndHeight(view: View, width: Int = 0, height: Int = 0) {
        val params = view.layoutParams
        if (width != 0) {
            params.width = width
        }
        if (height != 0) {
            params.height = height
        }
        view.layoutParams = params
    }
}
