package com.chenzb.recorder.utils

import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 创建者：Chenzb
 * 创建日期：2022/12/13 14:52
 * 描述：时间工具类
 */
object TimeUtils {

    fun formatHourMinSec(length: Long): String {
        val timeUnit = TimeUnit.MILLISECONDS
        val numHour = timeUnit.toHours(length)
        val numMinutes = timeUnit.toMinutes(length)
        val numSeconds = timeUnit.toSeconds(length)

        return if (numHour == 0L) {
            String.format(
                Locale.getDefault(),
                "%02d:%02d",
                numMinutes,
                numSeconds % 60)
        } else {
            String.format(
                Locale.getDefault(),
                "%02d:%02d:%02d",
                numHour,
                numMinutes % 60,
                numSeconds % 60
            )
        }
    }
}