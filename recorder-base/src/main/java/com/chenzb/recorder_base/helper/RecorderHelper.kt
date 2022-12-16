package com.chenzb.recorder_base.helper

import com.chenzb.recorder_base.utils.getDirPath
import com.chenzb.recorder_base.utils.getFileExtension
import com.chenzb.recorder_base.utils.getFileNameNoExtension
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

        val fileName = getFileNameNoExtension(path)
        val fileExtension = getFileExtension(path)
        var file = File("$fileDir${File.separator}$fileName.$fileExtension")

        var i = 1
        if (file.exists()) {
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
        } else {
            file.createNewFile()
        }

        return file
    }
}