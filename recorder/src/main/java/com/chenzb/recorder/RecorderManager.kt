package com.chenzb.recorder

import com.chenzb.recorder.presenter.impl.IRecorderPresenter

/**
 * 创建者：Chenzb
 * 创建日期：2022/12/11 20:48
 * 描述：录音管理类
 */
class RecorderManager : IRecorderPresenter {

    private lateinit var builder: Builder

    private lateinit var recorderPresenter: IRecorderPresenter

    constructor(): super()

    constructor(builder: Builder): super() {
        this.builder = builder
    }

    override fun startRecording() {
        recorderPresenter.startRecording()
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

        fun build(): IRecorderPresenter = RecorderManager(this)
    }
}