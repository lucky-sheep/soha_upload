package com.hunliji.mvvm.application

import android.app.Application
import android.content.Context
import com.bumptech.glide.Glide
import com.hunliji.integration_mw.AppDelegate

/**
 * @author kou_zhong
 * @date 2020-02-13.
 * description：
 */
class BaseApplication : Application() {
    private var appDelegate: AppDelegate? = null
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        if (appDelegate == null) {
            appDelegate = AppDelegate(base)
        }
    }

    override fun onCreate() {
        super.onCreate()
        appDelegate?.onCreate(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        appDelegate?.onTerminate(this)
    }
}
