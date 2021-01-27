package com.zyk.wifiautoproxy

import android.os.Build
import android.view.Gravity
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import androidx.annotation.RequiresApi

/**
 * ~/Library/Android/sdk/tools/bin/uiautomatorviewer
 * Created by zhangyakun on 2020/12/30.
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class WifiProxyAccessibilityService : BaseAccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && event.packageName == "com.android.settings") {
            val className = event.className
            if (className == "com.android.settings.Settings\$WifiSettingsActivity") {
                // wlan页面
                clickViewByID("com.android.settings:id/wifi_details")
                // 详情页面
                sleep()
                clickViewByText("高级设置")
                // 高级设置页面
                sleep()
                val spinner = findViewByText("代理服务器")?.parent?.getChild(1)
                performViewClick(spinner)
                sleep()
                if (WifiConfig.open) {
                    val manual = findViewByText("手动", true)
                    performViewClick(manual)
                    sleep()
                    val hostInfo = findViewByText("代理主机名")?.parent?.getChild(1)
                    inputText(hostInfo, WifiConfig.host)
                    val portInfo = findViewByText("代理服务器端口")?.parent?.getChild(1)
                    inputText(portInfo, WifiConfig.port)
                    clickViewByID("com.android.settings:id/save_button")
                    Toast.makeText(
                        this, "已经打开代理!\n ${WifiConfig.host}:${WifiConfig.port}", Toast
                            .LENGTH_LONG
                    ).apply { setGravity(Gravity.CENTER, 0, 0) }.show()
                } else {
                    val none = findViewByText("无", true)
                    performViewClick(none)
                    clickViewByID("com.android.settings:id/save_button")
                    Toast.makeText(this, "已经关闭代理", Toast.LENGTH_LONG)
                        .apply { setGravity(Gravity.CENTER, 0, 0) }.show()
                }
                performGlobalAction(GLOBAL_ACTION_HOME)
            }
        }
    }


}