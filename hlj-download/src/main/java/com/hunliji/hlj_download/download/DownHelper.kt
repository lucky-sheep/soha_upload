package com.hunliji.hlj_download.download

import androidx.collection.SparseArrayCompat
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.hunliji.hlj_download.download.model.DownLoadBuilder
import com.liulishuo.filedownloader.FileDownloadQueueSet
import com.liulishuo.filedownloader.FileDownloader
import kotlin.collections.ArrayList

/**
 * DownHelper
 *
 * @author wm
 * @date 20-3-2
 */
object DownHelper {
    fun downloadSingle(
        sourcePath: String,
        targetPath: String,
        init: DownLoadBuilder<String>.() -> Unit
    ) {
        val downLoadBuilder = DownLoadBuilder<String>().also(init)
        var perProgress = -1f
        downLoadBuilder.getStart()?.invoke()
        FileDownloader.getImpl().create(sourcePath)
            .setPath(targetPath)
            .setListener(object : FileDownloadListener() {
                override fun pending(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                    downLoadBuilder.getTask()?.invoke(task)
                    downLoadBuilder.getProgressSoFar2Total()
                        ?.invoke(task.smallFileSoFarBytes, task.smallFileTotalBytes)

                }

                override fun connected(
                    task: BaseDownloadTask?,
                    etag: String?,
                    isContinue: Boolean,
                    soFarBytes: Int,
                    totalBytes: Int
                ) {
                }

                override fun progress(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                    var fl = soFarBytes.toFloat() / totalBytes
                    if (fl > 1) {
                        fl = 1f
                    }
                    if (fl > perProgress) {
                        perProgress = fl
                        downLoadBuilder.getProgress()?.invoke(fl)
                    }
                    downLoadBuilder.getProgressSoFar2Total()
                        ?.invoke(task.smallFileSoFarBytes, task.smallFileTotalBytes)
                    downLoadBuilder.getTask()?.invoke(task)
                }

                override fun blockComplete(task: BaseDownloadTask?) {}

                override fun retry(
                    task: BaseDownloadTask?,
                    ex: Throwable?,
                    retryingTimes: Int,
                    soFarBytes: Int
                ) {
                }

                override fun completed(task: BaseDownloadTask) {
                    downLoadBuilder.getProgress()?.invoke(1f)
                    downLoadBuilder.getProgressSoFar2Total()
                        ?.invoke(task.smallFileSoFarBytes, task.smallFileTotalBytes)
                    downLoadBuilder.getTask()?.invoke(task)
                    downLoadBuilder.getSuccess()?.invoke(task.targetFilePath)
                }

                override fun paused(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {}

                override fun error(task: BaseDownloadTask, e: Throwable) {
                    downLoadBuilder.getError()?.invoke(e)
                }

                override fun warn(task: BaseDownloadTask) {}
            }).start()
    }

    fun downLoadGroupTogether(
        sourcePathList: List<String>,
        targetPathList: List<String>,
        init: DownLoadBuilder<List<String>>.() -> Unit
    ) {
        val downLoadBuilder = DownLoadBuilder<List<String>>().also(init)
        if (sourcePathList.isEmpty() || targetPathList.isEmpty()) {
            downLoadBuilder.getError()
                ?.invoke(Throwable(message = "sourcePathList或targetPathList为空"))
            return
        }
        if (sourcePathList.size != targetPathList.size) {
            downLoadBuilder.getError()
                ?.invoke(Throwable(message = "sourcePathList和targetPathList不相等"))
            return
        }
        var perProgress = -1f
        var successSize = 0
        val size = sourcePathList.size
        val taskProgress = SparseArrayCompat<Float>()
        var totalProgress: Float
        val queueSet = FileDownloadQueueSet(object : FileDownloadListener() {
            override fun warn(task: BaseDownloadTask) {
            }

            override fun completed(task: BaseDownloadTask) {
                successSize++
                if (successSize == size) {
                    taskProgress.clear()
                    totalProgress = 1f
                    if (totalProgress > perProgress) {
                        perProgress = totalProgress
                        downLoadBuilder.getProgress()?.invoke(totalProgress)

                    }
                    downLoadBuilder.getTask()?.invoke(task)
                    downLoadBuilder.getCount()?.invoke(successSize)
                    downLoadBuilder.getSuccess()?.invoke(targetPathList)
                } else {
                    taskProgress.put(task.id, 1f)
                    totalProgress = taskProgress.elements.average().toFloat()
                    if (totalProgress > 1f) {
                        totalProgress = 1f
                    }
                    if (totalProgress > perProgress) {
                        perProgress = totalProgress
                        downLoadBuilder.getProgress()?.invoke(totalProgress)
                    }
                    downLoadBuilder.getTask()?.invoke(task)
                    downLoadBuilder.getCount()?.invoke(successSize)
                }
            }

            override fun pending(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                downLoadBuilder.getTask()?.invoke(task)
            }

            override fun error(task: BaseDownloadTask, e: Throwable?) {
                e?.let {
                    downLoadBuilder.getError()?.invoke(it)
                }
            }

            override fun progress(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                var progress = soFarBytes.toFloat() / totalBytes
                if (progress > 1f) {
                    progress = 1f
                }
                taskProgress.put(task.id, progress)
                totalProgress = taskProgress.elements.average().toFloat()
                if (totalProgress > 1f) {
                    totalProgress = 1f
                }
                if (totalProgress > perProgress) {
                    perProgress = totalProgress
                    downLoadBuilder.getProgress()?.invoke(totalProgress)
                }
                downLoadBuilder.getTask()?.invoke(task)
            }

            override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            }
        })
        val tasks = ArrayList<BaseDownloadTask>()
        sourcePathList.forEachIndexed { currentIndex, srcPath ->
            tasks.add(
                FileDownloader.getImpl()
                    .create(srcPath)
                    .setPath(targetPathList[currentIndex])
                    .setTag(currentIndex + 1)
            )
        }
        queueSet.downloadTogether(tasks)
        queueSet.start()
        downLoadBuilder.getStart()?.invoke()
    }

    private val <T> SparseArrayCompat<T>.elements: List<T>
        get() {
            val elements = ArrayList<T>()
            for (i in 0 until this.size()) {
                val key = keyAt(i)
                val element = get(key)
                elements.add(element!!)
            }

            return elements
        }

    fun downloadGroupSequentially(
        sourcePathList: List<String>,
        targetPathList: List<String>,
        init: DownLoadBuilder<List<String>>.() -> Unit
    ) {
        val downLoadBuilder = DownLoadBuilder<List<String>>().also(init)
        if (sourcePathList.isEmpty() || targetPathList.isEmpty()) {
            downLoadBuilder.getError()
                ?.invoke(Throwable(message = "sourcePathList或targetPathList为空"))
            return
        }
        if (sourcePathList.size != targetPathList.size) {
            downLoadBuilder.getError()
                ?.invoke(Throwable(message = "sourcePathList和targetPathList不相等"))
            return
        }
        var perProgress = -1f
        var successSize = 0
        val size = sourcePathList.size
        val queueSet = FileDownloadQueueSet(object : FileDownloadListener() {
            override fun warn(task: BaseDownloadTask) {
            }

            override fun completed(task: BaseDownloadTask) {
                successSize++
                perProgress = -1f
                downLoadBuilder.getProgressSoFar2Total()
                    ?.invoke(task.smallFileSoFarBytes, task.smallFileTotalBytes)
                downLoadBuilder.getTask()?.invoke(task)
                downLoadBuilder.getCount()?.invoke(successSize)
                if (successSize == size) {
                    downLoadBuilder.getSuccess()?.invoke(targetPathList)
                }
            }

            override fun pending(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                downLoadBuilder.getProgressSoFar2Total()
                    ?.invoke(task.smallFileSoFarBytes, task.smallFileTotalBytes)
                downLoadBuilder.getTask()?.invoke(task)
            }

            override fun error(task: BaseDownloadTask, e: Throwable?) {
                e?.let {
                    downLoadBuilder.getError()?.invoke(it)
                }
            }

            override fun progress(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                var fl = soFarBytes.toFloat() / totalBytes
                if (fl > 1) {
                    fl = 1f
                }
                if (fl > perProgress) {
                    perProgress = fl
                    downLoadBuilder.getProgress()?.invoke(fl)
                }
                downLoadBuilder.getProgressSoFar2Total()
                    ?.invoke(task.smallFileSoFarBytes, task.smallFileTotalBytes)
                downLoadBuilder.getTask()?.invoke(task)
            }

            override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            }
        })
        val tasks = ArrayList<BaseDownloadTask>()
        sourcePathList.forEachIndexed { currentIndex, srcPath ->
            tasks.add(
                FileDownloader.getImpl()
                    .create(srcPath)
                    .setPath(targetPathList[currentIndex])
                    .setTag(currentIndex + 1)
            )
        }
        queueSet.downloadSequentially(tasks)
        queueSet.start()
        downLoadBuilder.getStart()?.invoke()
    }

    fun pauseAll() {
        FileDownloader.getImpl().pauseAll()
    }

    fun pauseByTaskId(taskId: Int = -1) {
        if (taskId != -1) {
            FileDownloader.getImpl().pause(taskId)
        }
    }
}
