package com.hunliji.mvvm.binding

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import androidx.databinding.BindingAdapter

/**
 * ImgBindAdapter
 *
 * @author wm
 * @date 20-2-18
 */
object ViewBindAdapter {
    @SuppressLint("CheckResult")
    @JvmStatic
    @BindingAdapter(
        "bgColor",
        "startColor",
        "endColor",
        "angle",
        "strokeColor",
        "strokeWidth",
        "pressColor",
        "pressStartColor",
        "pressEndColor",
        "pressAngle",
        "pressStrokeColor",
        "pressStrokeWidth",
        "half",
        "corner",
        "leftTopCorner",
        "rightTopCorner",
        "leftBottomCorner",
        "rightBottomCorner",
        requireAll = false
    )
    fun loadImage(
        view: View,
        bgColor: String? = null,
        startColor: String? = null,
        endColor: String? = null,
        angle: Int = 0,
        strokeColor: String? = null,
        strokeWidth: Float = 0f,
        pressColor: String? = null,
        pressStartColor: String? = null,
        pressEndColor: String? = null,
        pressAngle: Int = 0,
        pressStrokeColor: String? = null,
        pressStrokeWidth: Float = 0f,
        half: Boolean = false,
        corner: Int = 0,
        leftTopCorner: Int = 0,
        rightTopCorner: Int = 0,
        leftBottomCorner: Int = 0,
        rightBottomCorner: Int = 0
    ) {
        val realCorner = dp2px(view.context, corner)
        val realLeftTopCorner = dp2px(view.context, leftTopCorner)
        val realRightTopCorner = dp2px(view.context, rightTopCorner)
        val realLeftBottomCorner = dp2px(view.context, leftBottomCorner)
        val realRightBottomCorner = dp2px(view.context, rightBottomCorner)
        val realStrokeWidth = dp2px(view.context, strokeWidth)
        val realPressStrokeWidth = dp2px(view.context, pressStrokeWidth)
        val bg = createDrawable(
            startColor = startColor,
            endColor = endColor,
            angle = angle,
            color = bgColor,
            half = half,
            realCorner = realCorner,
            realLeftTopCorner = realLeftTopCorner,
            realRightTopCorner = realRightTopCorner,
            realLeftBottomCorner = realLeftBottomCorner,
            realRightBottomCorner = realRightBottomCorner,
            realStrokeWidth = realStrokeWidth,
            strokeColor = strokeColor
        )
        val pressBg = createDrawable(
            startColor = pressStartColor,
            endColor = pressEndColor,
            angle = pressAngle,
            color = pressColor,
            half = half,
            realCorner = realCorner,
            realLeftTopCorner = realLeftTopCorner,
            realRightTopCorner = realRightTopCorner,
            realLeftBottomCorner = realLeftBottomCorner,
            realRightBottomCorner = realRightBottomCorner,
            realStrokeWidth = realPressStrokeWidth,
            strokeColor = pressStrokeColor
        )
        val stateListDrawable = StateListDrawable().also {
            val pressed = android.R.attr.state_pressed
            it.addState(intArrayOf(-pressed), bg)
            it.addState(intArrayOf(pressed), pressBg)
            it.addState(intArrayOf(), bg)
        }
        view.background = stateListDrawable
    }
}

private fun createDrawable(
    startColor: String? = null,
    endColor: String? = null,
    angle: Int = 0,
    color: String? = null,
    half: Boolean = false,
    realCorner: Float = 0f,
    realLeftTopCorner: Float = 0f,
    realRightTopCorner: Float = 0f,
    realLeftBottomCorner: Float = 0f,
    realRightBottomCorner: Float = 0f,
    realStrokeWidth: Float = 0f,
    strokeColor: String? = null
): Drawable {
    return GradientDrawable().also {
        if (startColor.check() && endColor.check()) {
            val colors = intArrayOf(startColor.toColor(), endColor.toColor())
            it.colors = colors
            it.gradientType = GradientDrawable.LINEAR_GRADIENT
            it.orientation = getOrientation(angle)
        } else {
            if (color.check()) {
                it.setColor(color.toColor())
            }
        }
        if (half || realCorner > 0) {
            if (half) {
                it.cornerRadius = Int.MAX_VALUE.toFloat()
            } else {
                it.cornerRadius = realCorner
            }
        } else if (realLeftTopCorner > 0 || realRightTopCorner > 0
            || realLeftBottomCorner > 0 || realRightBottomCorner > 0
        ) {
            it.cornerRadii = floatArrayOf(
                realLeftTopCorner, realLeftTopCorner,
                realRightTopCorner, realRightTopCorner,
                realLeftBottomCorner, realLeftBottomCorner,
                realRightBottomCorner, realRightBottomCorner
            )
        }
        if (realStrokeWidth > 0 && strokeColor.check()) {
            it.setStroke(realStrokeWidth.toInt(), Color.parseColor(strokeColor))
        }
    }
}

private fun String?.check(): Boolean {
    return !TextUtils.isEmpty(this)
}

private fun dp2px(context: Context, dp: Int): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        context.resources.displayMetrics
    )
}

private fun dp2px(context: Context, dp: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics
    )
}

private fun String?.toColor(): Int {
    return try {
        Color.parseColor(this)
    } catch (e: Exception) {
        0
    }
}

/**
 * 获取GradientDrawable.Orientation 0-315逆时针旋转
 *
 * @param gradientOrientation 参数
 * @return GradientDrawable.Orientation
 */
private fun getOrientation(gradientOrientation: Int): GradientDrawable.Orientation {
    return when (gradientOrientation) {
        0 -> GradientDrawable.Orientation.LEFT_RIGHT
        45 -> GradientDrawable.Orientation.BL_TR
        90 -> GradientDrawable.Orientation.BOTTOM_TOP
        135 -> GradientDrawable.Orientation.BR_TL
        180 -> GradientDrawable.Orientation.RIGHT_LEFT
        225 -> GradientDrawable.Orientation.TR_BL
        270 -> GradientDrawable.Orientation.TOP_BOTTOM
        315 -> GradientDrawable.Orientation.TL_BR
        else -> GradientDrawable.Orientation.LEFT_RIGHT
    }
}


