package com.hunliji.mvvm.activity.activity2

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.hunliji.ext_master.dp
import com.hunliji.mvvm.BaseVm
import com.hunliji.mvvm.model.RefreshModel
import com.hunliji.mvvm.net.api.BannerBean
import com.hunliji.mvvm.net.api.HomeService
import com.hunliji.mvvm.net.exception.convert
import com.hunliji.mvvm.net.exception.convertList
import com.hunliji.mvvm.net.model.Zip2
import com.hunliji.mvvm.net.okhttpbuilder.RetrofitClient
import com.hunliji.recyclerview.observable.ObservableAdapterList
import kotlinx.coroutines.async

/**
 * Activity2Vm
 *
 * @author wm
 * @date 20-2-19
 */
class Activity2Vm(app: Application, map: MutableMap<String, @JvmSuppressWildcards Any>) :
    BaseVm(app, map) {
    var page = 0
    val list1 = ObservableAdapterList<Any>()
    private val retrofit by lazy {
        RetrofitClient.getInstance().create(HomeService::class.java)
    }
    val current = MutableLiveData<Int>().also {
        it.value = 0
    }
    val total = MutableLiveData<Int>().also {
        it.value = 250.dp
    }

    fun get(isNormal: Boolean, isRefresh: Boolean) {
        if (isRefresh) {
            page = 0
        }
        if (page == 0) {
            requestMix({
                val one = async {
                    retrofit.getHomeList(page)
                }
                val two = async {
                    retrofit.getBanner()
                }
                Zip2(
                    one.await().convertList(isRefresh, this@Activity2Vm),
                    two.await().convert(this@Activity2Vm)
                )
            }) {
                setNormal(isNormal)
                setRefresh(isRefresh)
                success {
                    val list = mutableListOf<Any>().also { list ->
                        it.two?.let {
                            list.add(HeaderItemVm(it))
                        }
                        it.one?.datas?.map { bean ->
                            list.add(
                                RefreshItemVm(
                                    RefreshModel(
                                        title = bean.desc ?: "无",
                                        img = bean.envelopePic ?: ""
                                    )
                                )
                            )
                        }
                    }
                    list1.setNewData(list)
                    page++
                }
            }
        } else {
            requestSingle({ retrofit.getHomeList(page) }) {
                setNormal(isNormal)
                setRefresh(isRefresh)
                success {
                    it.datas.map { bean ->
                        RefreshItemVm(
                            RefreshModel(
                                title = bean.desc ?: "无",
                                img = bean.envelopePic ?: ""
                            )
                        )
                    }.apply {
                        list1.addAll(this@apply)
                    }
                }
                page++
            }
        }
    }
}

class RefreshItemVm(bean: RefreshModel) {
    val title = MutableLiveData<String>()
    val imgUrl = MutableLiveData<String>()

    init {
        title.value = bean.title
        imgUrl.value = bean.img
    }
}

class HeaderItemVm(val bean: List<BannerBean>)
