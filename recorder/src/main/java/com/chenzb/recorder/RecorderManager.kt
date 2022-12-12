package com.chenzb.recorder

import android.content.Context
import com.chenzb.recorder.data.enum.RecorderFormat
import com.chenzb.recorder.presenter.M4aRecorderPresenter
import com.chenzb.recorder.presenter.impl.IRecorderPresenter

/**
 * 创建者：Chenzb
 * 创建日期：2022/12/11 20:48
 * 描述：录音管理类
 */
class RecorderManager(builder: Builder) : IRecorderPresenter {

    private var recorderPresenter: IRecorderPresenter

    init {
        this.recorderPresenter = when (builder.recordType) {
            RecorderFormat.M4A -> M4aRecorderPresenter()
        }
    }

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

    open class Builder {

        lateinit var recordType: RecorderFormat

        fun setMode(type: RecorderFormat): Builder {
            this.recordType = type
            return this
        }

        fun build(): IRecorderPresenter = RecorderManager(this)
    }
}