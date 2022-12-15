package com.chenzb.recorder_base.utils

/**
 * 创建者：Chenzb
 * 创建日期：2022/12/15 22:54
 * 描述：快速点击工具类
 */
class FastClickUtils {

    companion object {

        private var lastClickTime: Long = 0

        /**
         * 判断两次点击的间隔，如果小于diff，则认为是多次无效点击
         * @param diff 事件时间间隔
         */
        @JvmStatic
        fun isFastDoubleClick(diff: Long): Boolean {
            val time = System.currentTimeMillis()
            val timeD = time - lastClickTime

            if (lastClickTime > 0 && timeD < diff) {
                return true
            }

            lastClickTime = time

            return false
        }
    }
}