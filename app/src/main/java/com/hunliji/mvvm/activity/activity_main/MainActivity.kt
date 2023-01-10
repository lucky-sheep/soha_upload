package com.hunliji.mvvm.activity.activity_main

import android.content.Intent
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.hunliji.ext_master.deviceWidth
import com.hunliji.ext_master.launchActivity
import com.hunliji.mvvm.*
import com.hunliji.mvvm.activity.activity2.Activity2
import com.hunliji.mvvm.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity<ActivityMainBinding, MainVm>() {
    override fun getLayoutId() = R.layout.activity_main

    override fun getData(intent: Intent?) {
        setValue("id" to 1)
    }

    override fun initView() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.text -> {
                launchActivity<Activity2> { }
            }
            R.id.loading -> {
                showLoading()
            }
            R.id.success -> {
                hideLoading()
            }
            R.id.error -> {
                showError()
            }
            R.id.empty -> {
                showEmpty()
            }
        }
    }

    override fun onRequestReload() {
        showLoading()
    }

    override fun getRegisterLoading(): Any {
        return fragment_container
    }
}
