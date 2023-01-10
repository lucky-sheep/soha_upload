package com.hunliji.mvvm.activity.activity2

import android.content.res.Resources
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.hunliji.ext_master.launchActivity
import com.hunliji.mvvm.BaseActivity
import com.hunliji.mvvm.R
import com.hunliji.mvvm.databinding.Activity2Binding
import com.hunliji.recyclerview.ItemClickPresenter
import com.hunliji.recyclerview.adapter.MultiTypeAdapter
import kotlinx.android.synthetic.main.activity2.*

/**
 * Activity2
 *
 * @author wm
 * @date 20-2-19
 */
class Activity2 : BaseActivity<Activity2Binding, Activity2Vm>(), ItemClickPresenter<Any> {
    override fun getLayoutId() = R.layout.activity2
    private var mDy = 0

    companion object {
        const val HEADER = 0
        const val NORMAL = 1
    }

    private val adapter by lazy {
        MultiTypeAdapter(this, viewModel.list1, object : MultiTypeAdapter.MultiViewType {
            override fun getViewType(item: Any): Int {
                if (item is RefreshItemVm) {
                    return NORMAL
                }
                if (item is HeaderItemVm) {
                    return HEADER
                }
                throw Resources.NotFoundException("${item::class} 找不到相应的ViewType")
            }
        }).apply {
            addViewTypeToLayoutMap(HEADER, R.layout.refresh_banner)
            addViewTypeToLayoutMap(NORMAL, R.layout.refresh_item)
            itemPresenter = this@Activity2
            lifecycle = this@Activity2
        }
    }

    override fun onPrepare() {
        loadData(isNormal = true, isRefresh = true)
    }

    override fun initView() {
        with(recycler) {
            adapter = this@Activity2.adapter
            layoutManager = linearLayoutManager
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    mDy += dy
                    if (mDy < 0) {
                        mDy = 0
                    }
                    viewModel.current.value = mDy
                }
            })
        }
    }

    override fun loadData(isNormal: Boolean, isRefresh: Boolean) {
        viewModel.get(isNormal, isRefresh)
    }

    override fun onRequestReload() {
        loadData(isNormal = true, isRefresh = true)
    }

    override fun onItemClick(v: View, position: Int, item: Any) {
        when (v.id) {
            R.id.banner -> {
                launchActivity<DownLoadActivity>()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {

        }
    }
}
