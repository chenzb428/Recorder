package com.chenzb.recorder_m4a.config

import android.os.Environment
import java.io.File

/**
 * 创建者：Chenzb
 * 创建日期：2022/12/12 22:19
 * 描述：录音配置
 */
object RecorderConfig {

    /**
     * 文件存储文件夹
     */
    var FILE_SAVE_FOLDER_PATH = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)}${File.separator}recorder"

    /**
     * 文件存储名称
     */
    var FILE_NAME = "record"

    /**
     * m4a文件保存路径
     */
    val m4aSavePath = "$FILE_SAVE_FOLDER_PATH${File.separator}$FILE_NAME.m4a"
}