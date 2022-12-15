package com.chenzb.recorder_base

import android.content.Context
import com.chenzb.recorder_base.callback.RecorderCallback
import com.chenzb.recorder_base.presenter.impl.IRecorderPresenter

/**
 * 创建者：Chenzb
 * 创建日期：2022/12/11 20:48
 * 描述：录音管理类
 */
class RecorderManager<T: IRecorderPresenter>(builder: Builder<T>) : IRecorderPresenter {

    private var recorderPresenter: IRecorderPresenter

    init {
        this.recorderPresenter = builder.recorderPresenterClass.newInstance()
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

    override fun setRecorderCallback(callback: RecorderCallback) {
        recorderPresenter.setRecorderCallback(callback)
    }

    open class Builder<T: IRecorderPresenter> {

        lateinit var recorderPresenterClass: Class<T>

        fun setPresenter(recorderPresenterClass: Class<T>): Builder<T> {
            this.recorderPresenterClass = recorderPresenterClass
            return this
        }

        fun build(): RecorderManager<T> = RecorderManager(this)
    }
}