package com.zyk.wifiautoproxy

import androidx.annotation.StringDef
import com.zyk.wifiautoproxy.WifiConfig.ProxyType.Companion.CHARLES
import com.zyk.wifiautoproxy.WifiConfig.ProxyType.Companion.HASSAN

/**
 * Created by windherd on 2020/12/30.
 */
object WifiConfig {
    @JvmField
    var open: Boolean = false

    @JvmField
    var host: String = ""

    @JvmField
    var port: String = ""

    @JvmField
    @ProxyType
    var proxyType: String? = null

    @StringDef(CHARLES, HASSAN)
    annotation class ProxyType {
        companion object {
            const val CHARLES = "Charles"
            const val HASSAN = "Hassan"
        }
    }
}