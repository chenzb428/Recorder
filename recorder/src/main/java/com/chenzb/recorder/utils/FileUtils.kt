package com.chenzb.recorder.utils

import java.io.File

/**
 * 创建者：Chenzb
 * 创建日期：2022/12/12 22:35
 * 描述：文件工具类
 */

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
 * 获取文件的名称（包含文件扩展名）
 */
fun getFileName(filePath: String): String {
    if (filePath.isEmpty()) {
        return ""
    }

    val lastSep = filePath.lastIndexOf(File.separator)

    return if (lastSep == -1) {
        ""
    } else {
        filePath.substring(lastSep + 1)
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