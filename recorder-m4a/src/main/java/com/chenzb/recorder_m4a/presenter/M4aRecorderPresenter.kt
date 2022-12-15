package com.chenzb.recorder_m4a.presenter

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import com.chenzb.recorder_base.callback.RecorderCallback
import com.chenzb.recorder_base.helper.RecorderHelper
import com.chenzb.recorder_base.presenter.impl.IRecorderPresenter
import com.chenzb.recorder_m4a.config.RecorderConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 创建者：Chenzb
 * 创建日期：2022/12/11 22:53
 * 描述：录音：m4a格式
 */
class M4aRecorderPresenter : IRecorderPresenter {

    companion object {

        fun create(): M4aRecorderPresenter {
            return M4aRecorderPresenter()
        }
    }

    private var recorderCallback: RecorderCallback? = null

    private var mediaRecorder: MediaRecorder? = null

    private var outputFile: File? = null

    private var timer: Timer? = null

    private var isRecording = AtomicBoolean(false)
    private var isPaused = AtomicBoolean(false)

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
            release()
        }

        // 成功开始录制
        if (mediaRecorder != null) {
            updateTime = System.currentTimeMillis()
            updateRecordingTime()

            isRecording.set(true)
            isPaused.set(false)

            recorderCallback?.onStartRecord()
        }
    }

    override fun pauseRecording() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (mediaRecorder == null || !isRecording.get()) {
                return
            }

            if (!isPaused.get()) {
                try {
                    mediaRecorder?.pause()
                } catch (e: IllegalStateException) {
                    release()
                }

                if (mediaRecorder != null) {
                    totalRecordTime += System.currentTimeMillis() - updateTime
                    stopUpdateRecordingTime()

                    isPaused.set(true)

                    recorderCallback?.onPauseRecord()
                }
            }
        } else {
            stopRecording()
        }
    }

    override fun resumeRecording() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (mediaRecorder == null || !isPaused.get()) {
                return
            }

            try {
                mediaRecorder?.resume()
            } catch (e: IllegalStateException) {
                release()
            }

            if (mediaRecorder != null) {
                updateTime = System.currentTimeMillis()
                updateRecordingTime()

                isPaused.set(false)

                recorderCallback?.onResumeRecord()
            }
        }
    }

    override fun stopRecording() {
        if (mediaRecorder == null || !isRecording.get()) {
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            stopUpdateRecordingTime()

            try {
                mediaRecorder?.stop()
            } catch (e: RuntimeException) {
                release()
            }

            if (mediaRecorder != null) {
                isRecording.set(false)

                mediaRecorder?.release()

                CoroutineScope(Dispatchers.Main).launch {
                    recorderCallback?.onStopRecord(outputFile)

                    totalRecordTime = 0L
                    mediaRecorder = null
                    outputFile = null
                }
            }
        }
    }

    override fun cancelRecording() {
        Log.d("Chenzb", "M4aRecorderPresenter: cancelRecording.....")
    }

    override fun setRecorderCallback(callback: RecorderCallback?) {
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

    /**
     * 停止更新录音时间
     */
    private fun stopUpdateRecordingTime() {
        timer?.cancel()
        timer?.purge()
        timer = null

        updateTime = 0L
    }

    private fun release() {
        stopUpdateRecordingTime()

        totalRecordTime = 0L
        mediaRecorder = null
        outputFile = null
    }
}