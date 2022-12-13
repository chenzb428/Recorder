package com.chenzb.recorder.helper

import com.chenzb.recorder.utils.getDirPath
import com.chenzb.recorder.utils.getFileExtension
import com.chenzb.recorder.utils.getFileNameNoExtension
import java.io.File

/**
 * 创建者：Chenzb
 * 创建日期：2022/12/13 10:21
 * 描述：录音帮助类
 */
object RecorderHelper {

    /**
     * 检查录音文件及初始化
     */
    @JvmStatic
    fun createRecordFile(path: String): File? {
        // 检查文件夹存在
        val fileDir = File(getDirPath(path))
        if (!fileDir.exists()) {
            fileDir.mkdirs()
        }

        var file: File?
        val fileName = getFileNameNoExtension(path)
        val fileExtension = getFileExtension(path)
        var i = 1

        while (true) {
            val name = "$fileName-$i"

            file = File("$fileDir${File.separator}$name.$fileExtension")

            if (file.exists()) {
                i ++
            } else {
                file.createNewFile()
                break
            }
        }

        return file
    }
}