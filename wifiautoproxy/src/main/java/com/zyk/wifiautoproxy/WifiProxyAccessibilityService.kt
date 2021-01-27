package com.zyk.wifiautoproxy

import android.os.Build
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import androidx.annotation.RequiresApi

/**
 * ~/Library/Android/sdk/tools/bin/uiautomatorviewer
 * Created by windherd on 2020/12/30.
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class WifiProxyAccessibilityService : BaseAccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && event.packageName == "com.android.settings") {
            val className = event.className
            if (className == "com.android.settings.Settings\$WifiSettingsActivity") {
                // wlan页面
                clickViewByID("com.android.settings:id/wifi_details")
                // wifi详情页面
                sleep()
                clickViewByText("查看更多")
                val spinner = findViewByText("代理服务器")?.parent?.getChild(0)
                performViewClick(spinner)
                sleep()
                if (WifiConfig.open) {
                    val manual = findViewByText("手动", true)
                    performViewClick(manual)
                    //滚动
                    performScrollForward("com.android.settings:id/recycler_view")
                    sleep()
                    val hostInfo = findViewByText("代理主机名")?.parent?.getChild(1)
                    inputText(hostInfo, WifiConfig.host)
                    val portInfo = findViewByText("代理服务器端口")?.parent?.getChild(1)
                    inputText(portInfo, WifiConfig.port)
                    sleep(500)
                    clickViewByID("com.android.settings:id/save_button")
                    showActionNotice("已经打开${WifiConfig.proxyType}代理!\n ${WifiConfig.host}:${WifiConfig.port}")
                } else {
                    val none = findViewByText("无", true)
                    sleep(500)
                    performViewClick(none)
                    clickViewByID("com.android.settings:id/save_button")
                    showActionNotice("已经关闭代理")
                }
                performGlobalAction(GLOBAL_ACTION_HOME)
            }
        }
    }

    private fun showActionNotice(str: String) {
        Toast.makeText(this,str,Toast.LENGTH_LONG).show()
    }
}