package com.chenzb.recorder_base.config

import android.os.Environment
import java.io.File

/**
 * 创建者：Chenzb
 * 创建日期：2022/12/15 15:47
 * 描述：录音配置
 */
object RecorderConfig {

    /**
     * 文件存储文件夹
     */
    var SAVE_FOLDER_PATH = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)}${File.separator}recorder"

    /**
     * 文件存储名称
     */
    var SAVE_FILE_NAME = "record"

    var AUDIO_CHANNELS = 2
    var AUDIO_SAMPLING_RATE = 44100
    var AUDIO_ENCODING_BITRATE = 128000
}