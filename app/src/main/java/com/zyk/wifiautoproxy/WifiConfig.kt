package com.zyk.wifiautoproxy

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
    var proxyType: String? = null
}

const val BPROXY_PORT = "8888"
const val PROXY_MAN_PORT = "9090"
const val CHARLES_PORT = "8888"

open class Proxy(val host: String, val port: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val other1 = other as? Proxy ?: return false
        if (other1.host != host) return false
        if (other1.port != port) return false
        return true
    }

    override fun hashCode(): Int {
        var result = host.hashCode()
        result = 31 * result + port.hashCode()
        return result
    }
}

class BProxy(host: String) : Proxy(host, BPROXY_PORT)
class ProxyMan(host: String) : Proxy(host, PROXY_MAN_PORT)
class Charles(host: String) : Proxy(host, CHARLES_PORT)
