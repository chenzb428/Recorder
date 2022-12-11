package com.chenzb.recorder.presenter.impl

/**
 * 创建者：Chenzb
 * 创建日期：2022/12/11 20:51
 * 描述：录音功能接口
 */
interface IRecorderPresenter {

    fun startRecording()

    fun pauseRecording()

    fun resumeRecording()

    fun stopRecording()

    fun cancelRecording()
}