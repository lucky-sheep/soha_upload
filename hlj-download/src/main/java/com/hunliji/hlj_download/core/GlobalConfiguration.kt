package com.hunliji.hlj_download.core

import android.app.Application
import com.liulishuo.filedownloader.FileDownloader
import com.xueyu.applicationproxy.ConfigModule
import kotlin.properties.Delegates

/**
 * GlobalConfiguration
 *
 * @author wm
 * @date 20-3-2
 */
internal var app: Application by Delegates.notNull()

class GlobalConfiguration : ConfigModule {

    override fun injectApp(application: Application) {
        app = application
        FileDownloader.setup(app)
    }

    override fun terminateApp(application: Application) {

    }

    override fun getPriority() = 5
}