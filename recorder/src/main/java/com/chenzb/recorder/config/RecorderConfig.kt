package com.chenzb.recorder.config

import android.os.Environment
import java.io.File

/**
 * 创建者：Chenzb
 * 创建日期：2022/12/12 22:19
 * 描述：录音配置
 */
object RecorderConfig {

    var FILE_SAVE_FOLDER_PATH = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)}${File.separator}recorder"

    var FILE_NAME = "record"
}