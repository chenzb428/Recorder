package com.chenzb.recorder_m4a.presenter

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import com.chenzb.recorder_m4a.config.RecorderConfig
import com.chenzb.recorder_base.callback.RecorderCallback
import com.chenzb.recorder_base.helper.RecorderHelper
import com.chenzb.recorder_base.presenter.impl.IRecorderPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.Timer
import java.util.TimerTask

/**
 * 创建者：Chenzb
 * 创建日期：2022/12/11 22:53
 * 描述：录音：m4a格式
 */
class M4aRecorderPresenter : IRecorderPresenter {

    private var recorderCallback: RecorderCallback? = null

    private var mediaRecorder: MediaRecorder? = null

    private var outputFile: File? = null

    private var timer: Timer? = null

    private var updateTime: Long = 0L
    private var totalRecordTime: Long = 0L

    override fun startRecording(context: Context) {
        outputFile = RecorderHelper.createRecordFile(RecorderConfig.m4aSavePath)

        if (outputFile == null || !outputFile!!.exists() || !outputFile!!.isFile) {
            return
        }

        if (mediaRecorder == null) {
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                MediaRecorder()
            }
        }

        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder?.setAudioChannels(2)
        mediaRecorder?.setAudioSamplingRate(48000)
        mediaRecorder?.setAudioEncodingBitRate(256000)
        mediaRecorder?.setMaxDuration(-1)
        mediaRecorder?.setOutputFile(outputFile?.absolutePath)

        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
        } catch (e: Exception) {
            mediaRecorder = null
        }

        // 成功开始录制
        if (mediaRecorder != null) {
            updateTime = System.currentTimeMillis()
            updateRecordingTime()

            recorderCallback?.onStartRecord()
        }
    }

    override fun pauseRecording() {
        Log.d("Chenzb", "M4aRecorderPresenter: pauseRecording.....")
    }

    override fun resumeRecording() {
        Log.d("Chenzb", "M4aRecorderPresenter: resumeRecording.....")
    }

    override fun stopRecording() {
        CoroutineScope(Dispatchers.IO).launch {
            stopUpdateRecordingTime()

            try {
                mediaRecorder?.stop()
            } catch (e: RuntimeException) {
                e.printStackTrace()
            }

            // 释放 MediaRecorder
            mediaRecorder?.release()

            // 回调录音结果
            CoroutineScope(Dispatchers.Main).launch {
                recorderCallback?.onStopRecord(outputFile)

                // 释放资源
                totalRecordTime = 0L
                mediaRecorder = null
                outputFile = null
            }
        }
    }

    override fun cancelRecording() {
        Log.d("Chenzb", "M4aRecorderPresenter: cancelRecording.....")
    }

    override fun setRecorderCallback(callback: RecorderCallback) {
        this.recorderCallback = callback
    }

    /**
     * 更新录音时间
     */
    private fun updateRecordingTime() {
        if (timer == null) {
            timer = Timer()
        } else {
            timer?.cancel()
        }
        timer?.schedule(object : TimerTask() {

            override fun run() {
                if (mediaRecorder != null) {
                    val currentTimeMillis = System.currentTimeMillis()
                    totalRecordTime += currentTimeMillis - updateTime
                    updateTime = currentTimeMillis

                    recorderCallback?.onRecordingProgress(totalRecordTime, mediaRecorder!!.maxAmplitude)
                }
            }
        }, 0, 50L)
    }

    private fun stopUpdateRecordingTime() {
        timer?.cancel()
        timer?.purge()
        timer = null

        updateTime = 0L
    }
}