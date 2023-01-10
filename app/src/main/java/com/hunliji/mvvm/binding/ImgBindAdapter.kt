package com.hunliji.mvvm.binding

import android.annotation.SuppressLint
import android.graphics.*
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.request.RequestOptions
import com.hunliji.hlj_image.FitXYTransformation
import com.hunliji.hlj_image.RoundedCornersTransformation
import com.hunliji.hlj_image.utils.ImagePath
import android.graphics.Bitmap
import com.hunliji.hlj_image.CircleTransformation

/**
 * ImgBindAdapter
 *
 * @author wm
 * @date 19-8-29
 */
object ImgBindAdapter {
    @SuppressLint("CheckResult")
    @JvmStatic
    @BindingAdapter(
        "imageUrl",
        "placeHolder",
        "error",
        "circle",
        "lifecycle",
        "noAnimation",
        "corner",
        "scaleType",
        "cornerType",
        "width",
        "height",
        "placeholderOrErrorColor",
        "strokeWidth",
        "strokeColor",
        requireAll = false
    )
    fun loadImage(
        view: ImageView,
        url: Any? = null,
        placeholder: Drawable? = null,
        error: Drawable? = null,
        circle: Boolean = false,
        lifecycle: Any? = null,
        noAnimation: Boolean = false,
        corner: Int = 0,
        scaleType: ImageView.ScaleType? = null,
        cornerType: RoundedCornersTransformation.CornerType? = null,
        width: Int = 0,
        height: Int = 0,
        placeholderOrErrorColor: Int = 0,
        strokeWidth: Float = 0f,
        strokeColor: Int = 0
    ) {
        var realUrl = url
        (url as? String)?.let {
            if (width != 0 && height != 0 && Utils.isHttpUrl(url)) {
                realUrl = ImagePath.buildPath(url)
                    .width(width)
                    .height(height)
                    .cropPath()
            }
        }
        if (width != 0 && height != 0) {
            Utils.widthAndHeight(view, width, height)
        }
        val viewWidth = view.layoutParams.width
        val viewHeight = view.layoutParams.height
        val realCorner = Utils.dp2px(view.context, corner).toFloat()
        val realStrokeColor = if (strokeColor == 0) Color.parseColor("#e6e6e6") else strokeColor
        val realColor =
            if (placeholderOrErrorColor == 0) Color.parseColor("#e6e6e6") else placeholderOrErrorColor
        val realStroke = Utils.dp2px(view.context, strokeWidth)
        val load =
            when (lifecycle) {
                is FragmentActivity -> {
                    Glide.with(lifecycle)
                        .load(realUrl)
                }
                is Fragment -> {
                    Glide.with(lifecycle)
                        .load(realUrl)
                }
                else -> {
                    Glide.with(view.context)
                        .load(realUrl)
                }
            }
        val scaleTransform = scaleType?.toTransformation() ?: CenterCrop()
        val options = RequestOptions()
        if (noAnimation) {
            options.dontAnimate()
        }
        if (circle) {
            if (placeholder == null) {
                options.placeholder(
                    CircleTransformation(
                        realStroke,
                        realStrokeColor
                    ).createPlaceOrError(view.context, realColor, viewWidth, viewHeight)
                )
            } else {
                options.placeholder(placeholder)
            }
            if (error == null) {
                options.error(
                    CircleTransformation(
                        realStroke,
                        realStrokeColor
                    ).createPlaceOrError(view.context, realColor, viewWidth, viewHeight)
                )
            } else {
                options.error(error)
            }
        } else {
            if (placeholder == null) {
                options.placeholder(
                    RoundedCornersTransformation(
                        realCorner.toInt(),
                        realStroke,
                        cornerType ?: RoundedCornersTransformation.CornerType.ALL,
                        realStrokeColor
                    ).createPlaceOrError(view.context, realColor, viewWidth, viewHeight)
                )
            } else {
                options.placeholder(placeholder)
            }
            if (error == null) {
                options.placeholder(
                    RoundedCornersTransformation(
                        realCorner.toInt(),
                        realStroke,
                        cornerType ?: RoundedCornersTransformation.CornerType.ALL,
                        realStrokeColor
                    ).createPlaceOrError(view.context, realColor, viewWidth, viewHeight)
                )
            } else {
                options.error(error)
            }
        }
        val loadWithOptions = load.apply(options)
        if (circle) {
            loadWithOptions.transform(
                scaleTransform, MultiTransformation(
                    scaleTransform, CircleTransformation(
                        realStroke,
                        realStrokeColor
                    )
                )
            )
        } else {
            loadWithOptions.transform(
                MultiTransformation(
                    scaleTransform, RoundedCornersTransformation(
                        realCorner.toInt(),
                        realStroke,
                        cornerType ?: RoundedCornersTransformation.CornerType.ALL,
                        realStrokeColor
                    )
                )
            )
        }
        loadWithOptions.into(view)
    }
}


private fun ImageView.ScaleType.toTransformation(): Transformation<Bitmap>? {
    return when (this) {
        ImageView.ScaleType.CENTER_INSIDE -> {
            CenterInside()
        }
        ImageView.ScaleType.FIT_CENTER -> {
            FitCenter()
        }
        ImageView.ScaleType.FIT_XY -> {
            FitXYTransformation()
        }
        ImageView.ScaleType.CENTER_CROP -> {
            CenterCrop()
        }
        else -> {
            CenterCrop()
        }
    }
}





