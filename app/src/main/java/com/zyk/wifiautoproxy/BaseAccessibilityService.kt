package com.zyk.wifiautoproxy

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Created by windherd on 2020/12/30.
 */
abstract class BaseAccessibilityService : AccessibilityService() {
    companion object {
        /**
         * Check当前辅助服务是否启用
         *
         * @param serviceName serviceName
         * @return 是否启用
         */
        fun checkAccessibilityEnabled(context: Context, serviceName: String): Boolean {
            val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as
                    AccessibilityManager
            val accessibilityServices =
                accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
            for (info in accessibilityServices) {
                if (info.resolveInfo?.serviceInfo?.name == serviceName) {
                    return true
                }
            }
            return false
        }

        /**
         * 前往开启辅助服务界面
         */
        fun goAccess(context: Context) {
            context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }

        fun sleep(timeMillis: Long = 300) {
            try {
                Thread.sleep(timeMillis)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 模拟返回操作
     */
    fun performBackClick() {
        try {
            sleep(500)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        performGlobalAction(GLOBAL_ACTION_BACK)
    }

    /**
     * 模拟home 键操作
     */
    fun performHomeClick() {
        try {
            sleep(500)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        performGlobalAction(GLOBAL_ACTION_HOME)
    }

    /**
     * 模拟下滑操作
     */
    fun performScrollBackward(id: String?) {
        try {
            sleep()
            findViewByID(id)?.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    /**
     * 模拟上滑操作
     */
    fun performScrollForward(id: String?) {
        try {
            sleep()
            findViewByID(id)?.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    /**
     * 查找对应文本的View
     *
     * @param text text
     * @return View
     */
    @JvmOverloads
    fun findViewByText(
        text: String?,
        clickable: Boolean = false
    ): AccessibilityNodeInfo? {
        val accessibilityNodeInfo = rootInActiveWindow ?: return null
        val nodeInfoList =
            accessibilityNodeInfo.findAccessibilityNodeInfosByText(text)
        if (nodeInfoList != null && nodeInfoList.isNotEmpty()) {
            for (nodeInfo in nodeInfoList) {
                if (nodeInfo != null && nodeInfo.isClickable == clickable) {
                    return nodeInfo
                }
            }
        }
        return null
    }

    /**
     * 查找对应ID的View
     *
     * @param id id
     * @return View
     */
    fun findViewByID(id: String?): AccessibilityNodeInfo? {
        if (id == null) return null
        val accessibilityNodeInfo = rootInActiveWindow ?: return null
        val nodeInfoList =
            accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id)
        if (nodeInfoList != null && nodeInfoList.isNotEmpty()) {
            for (nodeInfo in nodeInfoList) {
                if (nodeInfo != null) {
                    return nodeInfo
                }
            }
        }
        return null
    }

    fun clickViewByText(text: String?) {
        val accessibilityNodeInfo = rootInActiveWindow ?: return
        val nodeInfoList =
            accessibilityNodeInfo.findAccessibilityNodeInfosByText(text)
        if (nodeInfoList != null && nodeInfoList.isNotEmpty()) {
            for (nodeInfo in nodeInfoList) {
                if (nodeInfo != null) {
                    performViewClick(nodeInfo)
                    break
                }
            }
        }
    }

    fun clickViewByID(id: String?) {
        if (id == null) return
        val accessibilityNodeInfo = rootInActiveWindow ?: return
        val nodeInfoList =
            accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id)
        if (nodeInfoList != null && nodeInfoList.isNotEmpty()) {
            for (nodeInfo in nodeInfoList) {
                if (nodeInfo != null) {
                    performViewClick(nodeInfo)
                    break
                }
            }
        }
    }

    /**
     * 模拟点击事件
     *
     * @param nodeInfo nodeInfo
     */
    fun performViewClick(nodeInfo: AccessibilityNodeInfo?) {
        var nodeInfo: AccessibilityNodeInfo? = nodeInfo ?: return
        while (nodeInfo != null) {
            if (nodeInfo.isClickable) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                break
            }
            nodeInfo = nodeInfo.parent
        }
    }

    /**
     * 模拟输入
     *
     * @param nodeInfo nodeInfo
     * @param text     text
     */
    fun inputText(nodeInfo: AccessibilityNodeInfo?, text: String?) {
        if (nodeInfo == null) return
        val arguments = Bundle()
        arguments.putCharSequence(
            AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
            text
        )
        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
    }

    override fun onInterrupt() {}
}