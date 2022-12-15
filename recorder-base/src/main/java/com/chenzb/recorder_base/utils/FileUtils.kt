package com.chenzb.recorder_base.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

/**
 * 创建者：Chenzb
 * 创建日期：2022/12/12 22:35
 * 描述：文件工具类
 */

/**
 * 删除文件
 */
fun deleteFile(context: Context, filePath: String?): Boolean {
    if (filePath.isNullOrEmpty()) {
        return false
    }

    if (!isFileExist(context, filePath)) {
        return false
    }

    return File(filePath).delete()
}

/**
 * 检查文件是否存在
 */
fun isFileExist(context: Context, filePath: String?): Boolean {
    if (filePath.isNullOrEmpty()) {
        return false
    }

    if (File(filePath).exists()) {
        return true
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        try {
            val uri = Uri.parse(filePath)
            val cr: ContentResolver = context.contentResolver
            val afd = cr.openAssetFileDescriptor(uri, "r") ?: return false

            try {
                afd.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } catch (e: FileNotFoundException) {
            return false
        }

        return true
    }

    return false
}

/**
 * 获取父文件夹的路径
 */
fun getDirPath(filePath: String?, separator: String = File.separator): String {
    if (filePath.isNullOrEmpty()) {
        return ""
    }

    val lastSep = filePath.lastIndexOf(separator)

    return if (lastSep == -1) {
        ""
    } else {
        filePath.substring(0, lastSep)
    }
}

/**
 * 通过路径获取文件名，不包括文件扩展名
 */
fun getFileNameNoExtension(filePath: String?): String {
    if (filePath.isNullOrEmpty()) {
        return ""
    }

    val lastPoi = filePath.lastIndexOf('.')
    val lastSep = filePath.lastIndexOf(File.separator)

    if (lastSep == -1) {
        return if (lastPoi == -1) filePath else filePath.substring(0, lastPoi)
    }

    return if (lastPoi == -1 || lastSep > lastPoi) {
        filePath.substring(lastSep + 1)
    } else {
        filePath.substring(lastSep + 1, lastPoi)
    }
}

/**
 * 获取文件扩展名
 */
fun getFileExtension(filePath: String?): String {
    if (filePath.isNullOrEmpty()) {
        return ""
    }

    val lastPoi = filePath.lastIndexOf('.')
    val lastSep = filePath.lastIndexOf(File.separator)

    return if (lastPoi == -1 || lastSep >= lastPoi) {
        ""
    } else {
        filePath.substring(lastPoi + 1)
    }
}