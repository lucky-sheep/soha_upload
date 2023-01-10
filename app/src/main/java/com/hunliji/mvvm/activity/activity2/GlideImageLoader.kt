package com.hunliji.mvvm.activity.activity2

import android.content.Context
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.hunliji.mvvm.net.api.BannerBean
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.youth.banner.loader.ImageLoader

/**
 * GlideImageLoader
 *
 * @author wm
 * @date 20-2-26
 */
class GlideImageLoader : ImageLoader() {
    override fun displayImage(context: Context, path: Any, imageView: ImageView) {
        if (path is BannerBean) {
            Glide.with(context).load(path.imagePath).into(imageView)
        }
    }
}
