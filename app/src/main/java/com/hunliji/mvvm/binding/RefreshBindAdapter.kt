package com.hunliji.mvvm.binding

import androidx.databinding.BindingAdapter
import com.hunliji.hlj_refresh.annotation.RefreshType
import com.hunliji.hlj_refresh.RefreshPresenter
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener

/**
 * RefreshBindAdapter
 *
 * @author wm
 * @date 19-9-3
 */
object RefreshBindAdapter {

    @JvmStatic
    @BindingAdapter("refreshing")
    fun bindRefreshing(smartRefreshLayout: SmartRefreshLayout, @RefreshType refreshType: Int?) {
        when (refreshType) {
            RefreshType.REFRESH_FINISH -> {
                smartRefreshLayout.finishRefresh()
                smartRefreshLayout.setNoMoreData(false)
            }
            RefreshType.REFRESH_FAIL -> {
                smartRefreshLayout.finishRefresh(false)
            }
            RefreshType.REFRESH_NOT_MORE -> {
                smartRefreshLayout.finishLoadMoreWithNoMoreData()
            }
            RefreshType.LOAD_MORE_FINISH -> {
                smartRefreshLayout.finishLoadMore()
            }
            RefreshType.LOAD_MORE_FAIL -> {
                smartRefreshLayout.finishLoadMore(false)
            }
            RefreshType.LOAD_NOT_MORE -> {
                smartRefreshLayout.finishRefresh()
                smartRefreshLayout.finishLoadMoreWithNoMoreData()
            }
        }
    }

    @JvmStatic
    @BindingAdapter("onRefresh")
    fun bindOnRefresh(smartRefreshLayout: SmartRefreshLayout, listener: RefreshPresenter?) {
        smartRefreshLayout.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onLoadMore(refreshLayout: RefreshLayout) {
                listener?.loadData(isRefresh = false)
            }

            override fun onRefresh(refreshLayout: RefreshLayout) {
                smartRefreshLayout.setNoMoreData(false)
                listener?.loadData(isRefresh = true)
            }
        })
    }
}
