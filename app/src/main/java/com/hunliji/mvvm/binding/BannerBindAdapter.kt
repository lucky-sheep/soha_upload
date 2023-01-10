package com.hunliji.mvvm.binding

import android.annotation.SuppressLint
import androidx.databinding.BindingAdapter
import com.hunliji.mvvm.activity.activity2.GlideImageLoader
import com.hunliji.mvvm.net.api.BannerBean
import com.hunliji.recyclerview.ItemClickPresenter
import com.youth.banner.Banner

/**
 * BannerBindAdapter
 *
 * @author wm
 * @date 20-2-26
 */
object BannerBindAdapter {
    @SuppressLint("CheckResult")
    @JvmStatic
    @BindingAdapter("list", "click", requireAll = false)
    fun bindData(
        view: Banner,
        list: List<BannerBean>, presenter: ItemClickPresenter<Any>?
    ) {
        view.setImageLoader(GlideImageLoader())
        view.setImages(list)
        view.setOnBannerListener {
            presenter?.onItemClick(view, it, list[it])
        }
        view.start()
    }
}
