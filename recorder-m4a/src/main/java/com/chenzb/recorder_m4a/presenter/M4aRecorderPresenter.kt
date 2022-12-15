package com.chenzb.recorder_m4a.presenter

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import com.chenzb.recorder_base.callback.RecorderCallback
import com.chenzb.recorder_base.config.RecorderConfig
import com.chenzb.recorder_base.helper.RecorderHelper
import com.chenzb.recorder_base.presenter.impl.IRecorderPresenter
import com.chenzb.recorder_base.utils.FastClickUtils
import com.chenzb.recorder_base.utils.deleteFile
import com.chenzb.recorder_m4a.utils.MediaUtils
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

        val savePath = "${RecorderConfig.SAVE_FOLDER_PATH}${File.separator}${RecorderConfig.SAVE_FILE_NAME}.m4a"

        fun create(): M4aRecorderPresenter {
            return M4aRecorderPresenter()
        }
    }

    private var context: Context? = null

    private var recorderCallback: RecorderCallback? = null

    private var mediaRecorder: MediaRecorder? = null

    /**
     * 录音输出文件
     */
    private var outputFile: File? = null

    /**
     * 录音缓存文件
     */
    private var listTempPaths: MutableList<String>? = null

    private var timer: Timer? = null

    /**
     * 录音是否进行中
     */
    private var isRecording = AtomicBoolean(false)

    /**
     * 录音是否暂停
     */
    private var isPaused = AtomicBoolean(false)

    /**
     * 记录当前时间
     */
    private var updateTime: Long = 0L

    /**
     * 录音总时长
     */
    private var totalRecordTime: Long = 0L

    override fun startRecording(context: Context) {
        this.context = context
        outputFile = RecorderHelper.createRecordFile(savePath)

        if (!isRecording.get()) {
            listTempPaths = mutableListOf()
        }

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
        mediaRecorder?.setAudioChannels(RecorderConfig.AUDIO_CHANNELS)
        mediaRecorder?.setAudioSamplingRate(RecorderConfig.AUDIO_SAMPLING_RATE)
        mediaRecorder?.setAudioEncodingBitRate(RecorderConfig.AUDIO_ENCODING_BITRATE)
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

            FastClickUtils.isFastDoubleClick(500L)

            // 判断是否为暂停侯继续录制
            if (isRecording.get()) {
                recorderCallback?.onResumeRecord()
            } else {
                recorderCallback?.onStartRecord()
            }

            isRecording.set(true)
            isPaused.set(false)
        }
    }

    override fun pauseRecording() {
        if (mediaRecorder == null || !isRecording.get()) {
            return
        }

        if (FastClickUtils.isFastDoubleClick(500L)) {
            return
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
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
            pauseRecordingByStop()
        }
    }

    override fun resumeRecording() {
        if (!isPaused.get()) {
            return
        }

        if (FastClickUtils.isFastDoubleClick(500L)) {
            return
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (mediaRecorder == null) {
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
        } else {
            if (context != null) {
                startRecording(context!!)
            }
        }
    }

    override fun stopRecording() {
        if (!isRecording.get() || totalRecordTime < 1000L) {
            return
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (mediaRecorder == null) {
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
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                stopUpdateRecordingTime()

                try {
                    mediaRecorder?.stop()
                } catch (e: RuntimeException) {
                    release()
                }

                isRecording.set(false)

                mediaRecorder?.release()

                if (!this@M4aRecorderPresenter.isPaused.get()) {
                    listTempPaths?.add(outputFile!!.absolutePath)
                }

                if (!listTempPaths.isNullOrEmpty()) {
                    outputFile = RecorderHelper.createRecordFile(savePath)
                    MediaUtils.mergeMediaFiles(true, listTempPaths!!, outputFile!!.absolutePath)

                    listTempPaths?.forEach {
                        deleteFile(context!!, it)
                    }
                }

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

    private fun pauseRecordingByStop() {
        CoroutineScope(Dispatchers.IO).launch {
            totalRecordTime += System.currentTimeMillis() - updateTime
            stopUpdateRecordingTime()

            try {
                mediaRecorder?.stop()
            } catch (e: RuntimeException) {
                release()
            }

            if (mediaRecorder != null) {
                isPaused.set(true)

                mediaRecorder?.release()

                if (outputFile != null) {
                    listTempPaths?.add(outputFile!!.absolutePath)
                }

                CoroutineScope(Dispatchers.Main).launch {
                    recorderCallback?.onPauseRecord()
                }

                mediaRecorder = null
            }
        }
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

                    recorderCallback?.onRecordingProgress(totalRecordTime, mediaRecorder?.maxAmplitude ?: 0)
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