package com.hunliji.mvvm.activity.activity2

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.hunliji.hlj_download.download.DownHelper
import com.hunliji.mvvm.BaseVm
import com.hunliji.mvvm.model.DownItem
import com.hunliji.recyclerview.observable.ObservableAdapterList

class DownLoadVm(app: Application, map: MutableMap<String, @JvmSuppressWildcards Any>) :
    BaseVm(app, map) {
    val list = ObservableAdapterList<Any>()

    fun downloadGroupTogether() {
        DownHelper.downLoadGroupTogether(
            mutableListOf<String>().also { mutableList ->
                list.forEach {
                    if (it is DownLoadItemVm) {
                        mutableList.add(it.bean.downUrl)
                    }
                }
            },
            mutableListOf<String>().also { mutableList ->
                list.forEach {
                    if (it is DownLoadItemVm) {
                        mutableList.add(it.bean.targetUrl)
                    }
                }
            }
        ) {
            task { task ->
                list.find {
                    (it as? DownLoadItemVm)?.bean?.downUrl == task.url
                }?.let {
                    (it as? DownLoadItemVm)?.apply {
                        taskIdLD.value = task.id
                        if (task.smallFileTotalBytes > 0) {
                            progressLD.value =
                                (task.smallFileSoFarBytes * 100 / task.smallFileTotalBytes)
                            Log.e("test", "progress:${progressLD.value}")
                            bytes.value =
                                "${task.smallFileSoFarBytes}/${task.smallFileTotalBytes}"
                        }
                    }
                }
            }
            success {
                Log.e("test", "success")
            }
        }
    }
}

class DownLoadItemVm(val bean: DownItem) {
    val progressLD = MutableLiveData<Int>()
    val taskIdLD = MutableLiveData<Int>()
    val bytes = MutableLiveData<String>()

    init {
        taskIdLD.value = -1
        bytes.value = "0/0"
    }
}