package com.chenzb.recorder.presenter.impl

import android.content.Context

/**
 * 创建者：Chenzb
 * 创建日期：2022/12/11 20:51
 * 描述：录音功能接口
 */
interface IRecorderPresenter {

    /**
     * 开始录音
     */
    fun startRecording(context: Context)

    /**
     * 暂停录音
     */
    fun pauseRecording()

    /**
     * 恢复录音
     */
    fun resumeRecording()

    /**
     * 停止录音，进行保存
     */
    fun stopRecording()

    /**
     * 取消录音
     */
    fun cancelRecording()
}