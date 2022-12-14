package com.chenzb.recorder_base.callback

import java.io.File

/**
 * 创建者：Chenzb
 * 创建日期：2022/12/13 10:40
 * 描述：录音回调
 */
interface RecorderCallback {

    fun onStartRecord()

    fun onPauseRecord()

    fun onResumeRecord()

    fun onStopRecord(file: File?)

    fun onCancelRecord()

    fun onRecordingProgress(progress: Long, amplitude: Int)
}