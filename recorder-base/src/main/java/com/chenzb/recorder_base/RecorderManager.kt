package com.chenzb.recorder_base

import android.content.Context
import com.chenzb.recorder_base.callback.RecorderCallback
import com.chenzb.recorder_base.config.RecorderConfig
import com.chenzb.recorder_base.presenter.impl.IRecorderPresenter

/**
 * 创建者：Chenzb
 * 创建日期：2022/12/11 20:48
 * 描述：录音管理类
 */
class RecorderManager(builder: Builder) : IRecorderPresenter {

    private var recorderPresenter: IRecorderPresenter = builder.recorderPresenter

    override fun startRecording(context: Context) {
        recorderPresenter.startRecording(context)
    }

    override fun pauseRecording() {
        recorderPresenter.pauseRecording()
    }

    override fun resumeRecording() {
        recorderPresenter.resumeRecording()
    }

    override fun stopRecording() {
        recorderPresenter.stopRecording()
    }

    override fun cancelRecording() {
        recorderPresenter.cancelRecording()
    }

    override fun setRecorderCallback(callback: RecorderCallback?) {
        recorderPresenter.setRecorderCallback(callback)
    }

    open class Builder {

        lateinit var recorderPresenter: IRecorderPresenter

        fun setPresenter(recorderPresenter: IRecorderPresenter): Builder {
            this.recorderPresenter = recorderPresenter
            return this
        }

        fun setRecorderCallback(callback: RecorderCallback?): Builder {
            this.recorderPresenter.setRecorderCallback(callback)
            return this
        }

        fun setAudioChannels(audioChannels: Int): Builder {
            RecorderConfig.AUDIO_CHANNELS = audioChannels
            return this
        }

        fun setAudioSamplingRate(samplingRate: Int): Builder {
            RecorderConfig.AUDIO_SAMPLING_RATE = samplingRate
            return this
        }

        fun setAudioEncodingBitRate(bitRate: Int): Builder {
            RecorderConfig.AUDIO_ENCODING_BITRATE = bitRate
            return this
        }

        fun build(): RecorderManager = RecorderManager(this)
    }
}