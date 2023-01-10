package com.hunliji.mvvm.activity.activity2

import android.util.Log
import android.view.View
import com.hunliji.ext_master.toSdCardFilePath
import com.hunliji.hlj_download.download.DownHelper
import com.hunliji.hlj_download.upload.UploadHelper
import com.hunliji.hlj_download.upload.model.UpLoadInterface
import com.hunliji.mvvm.BaseActivity
import com.hunliji.mvvm.R
import com.hunliji.mvvm.databinding.ActivityDownloadBinding
import com.hunliji.mvvm.model.DownItem
import com.hunliji.mvvm.net.okhttpbuilder.RetrofitClient
import com.hunliji.recyclerview.ItemClickPresenter
import com.hunliji.recyclerview.adapter.SingleTypeAdapter
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import kotlinx.android.synthetic.main.activity_download.*
import java.io.File
import java.lang.StringBuilder

class DownLoadActivity : BaseActivity<ActivityDownloadBinding, DownLoadVm>(),
    ItemClickPresenter<Any> {
    override fun getLayoutId() = R.layout.activity_download

    override fun onPrepare() {
        loadData(isNormal = true, isRefresh = true)
    }

//    private val retrofit1 by lazy{
//        RetrofitClient().retrofit1.create(HomeService::class.java)
//    }

    private val adapter by lazy {
        SingleTypeAdapter(this, R.layout.item_download, viewModel.list)
            .apply {
                itemPresenter = this@DownLoadActivity
                lifecycle = this@DownLoadActivity
            }
    }

    override fun initView() {
        with(recycler) {
            layoutManager = linearLayoutManager
            adapter = this@DownLoadActivity.adapter
        }
    }

    override fun loadData(isNormal: Boolean, isRefresh: Boolean) {
        viewModel.list.setNewData(
            listOf(
                DownLoadItemVm(
                    DownItem(
                        "第一个",
                        "https://mv.hunliji.com/o_1duhdgo3l17ed1qa319eq132dqffu.jpg",
                        "https://mv.hunliji.com/o_1duhdgqbfdoqgdf1b37jog1du13.mp4",
                        "abc/down0.mp4".toSdCardFilePath()
                    )
                ),
                DownLoadItemVm(
                    DownItem(
                        "第二个",
                        "https://mv.hunliji.com/o_1duhh2nd81mm2mn31njsmtop5u2n.jpg",
                        "https://mv.hunliji.com/o_1duhh2qg31q6hqbb169s18rpn7c2s.mp4",
                        "abc/down1.mp4".toSdCardFilePath()
                    )
                ),
                DownLoadItemVm(
                    DownItem(
                        "第三个",
                        "https://mv.hunliji.com/o_1duhh2nd81mm2mn31njsmtop5u2n.jpg",
                        "https://mv.hunliji.com/o_1duhh2nd81mm2mn31njsmtop5u2n.jpg",
                        "abc/down2.jpg".toSdCardFilePath()
                    )
                )
            )
        )
    }

    class UpLoad(val path: String, val from: String = "", val tokenPath: String? = null) :
        UpLoadInterface {

        override fun source(): String {
            return path
        }

        override fun from() = from

        override fun tokenPath() = tokenPath
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.start -> {
                AndPermission.with(this)
                    .runtime()
                    .permission(Permission.Group.STORAGE)
                    .onGranted {
                        viewModel.downloadGroupTogether()
                    }.start()
            }
            R.id.upload -> {
                UploadHelper.setUp(RetrofitClient().retrofit1)
                UploadHelper.uploadSingle(
                    File("abc/down0.mp4".toSdCardFilePath())
                ) {
                    setCompress(true)
                    success {
                        Log.e("test",it.url)
                    }
                    error {
                        Log.e("test", "error:${it.message}")
                    }
                    progress { l, l2 ->
                        if (l2 > 0) {
                            val progress = (l * 100.toFloat() / l2).toInt()
                            Log.e("test", "upload:$progress")
                        }
                        Log.e("test", "l:${l}")
                        Log.e("test", "l2:${l2}")
                    }
                }
            }
            R.id.upload_group -> {
                UploadHelper.setUp(RetrofitClient().retrofit1)
                UploadHelper.uploadGroup(
                    listOf(
                        UpLoad(
                            "abc/down0.mp4".toSdCardFilePath(),
                            "MerchandiseItemExample",
                            "p/wedding/home/APIUtils/video_upload_token"
                        ),
                        UpLoad("abc/down2.jpg".toSdCardFilePath(), "Video")
                    )
                ) {
                    setCompress(true)
                    start {
                    }
                    success {
                        val builder = StringBuilder()
                        it.forEach { item ->
                            Log.e("test", "upload:${item.url}")
                        }
                        Log.e("test", "upload:$builder")
                    }
                    progress { soFar, total ->
                        if (total > 0) {
                            val progress = (soFar * 100.toFloat() / total).toInt()
                            Log.e("test", "upload:$progress")
                        }
                        Log.e("test", "l:${soFar}")
                        Log.e("test", "l2:${total}")
                    }
                    count {
                        Log.e("test", "count:${it}")
                    }
                    error {
                        Log.e("test", "error:${it.message}")
                    }
                }
            }
            R.id.cancel -> {
                UploadHelper.cancel()
            }
        }
    }

    override fun onItemClick(v: View, position: Int, item: Any) {
        when (v.id) {
            R.id.item_main -> {
                (item as? DownLoadItemVm)?.let {
                    it.taskIdLD.value?.let { taskId ->
                        if (taskId != -1) {
                            DownHelper.pauseByTaskId(taskId)
                        }
                    }
                }
            }
        }
    }
}