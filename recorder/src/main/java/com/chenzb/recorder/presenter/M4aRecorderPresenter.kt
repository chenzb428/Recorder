package com.chenzb.recorder.presenter

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import com.chenzb.recorder.callback.RecorderCallback
import com.chenzb.recorder.config.RecorderConfig
import com.chenzb.recorder.helper.RecorderHelper
import com.chenzb.recorder.presenter.impl.IRecorderPresenter
import java.io.File

/**
 * 创建者：Chenzb
 * 创建日期：2022/12/11 22:53
 * 描述：录音：m4a格式
 */
class M4aRecorderPresenter : IRecorderPresenter {

    private var recorderCallback: RecorderCallback? = null

    private var mediaRecorder: MediaRecorder? = null

    private var outputFile: File? = null

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
        try {
            mediaRecorder?.stop()
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }

        // 释放 MediaRecorder
        mediaRecorder?.release()
        // 回调录音结果
        recorderCallback?.onStopRecord(outputFile)

        // 释放资源
        mediaRecorder = null
        outputFile = null
    }

    override fun cancelRecording() {
        Log.d("Chenzb", "M4aRecorderPresenter: cancelRecording.....")
    }

    override fun setRecorderCallback(callback: RecorderCallback) {
        this.recorderCallback = callback
    }
}