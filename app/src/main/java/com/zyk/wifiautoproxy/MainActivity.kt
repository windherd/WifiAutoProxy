package com.zyk.wifiautoproxy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zyk.wifiautoproxy.Util.hideKeyboard
import com.zyk.wifiautoproxy.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val sp by lazy { getSharedPreferences("wifi_auto_proxy", Context.MODE_PRIVATE) }
    private val ipBproxy get() = binding.ipBproxy
    private val ipProxyMan get() = binding.ipProxyMan
    private val ipCharles get() = binding.ipCharles
    private val swBproxy get() = binding.swBproxy
    private val swProxyMan get() = binding.swProxyMan
    private val swCharles get() = binding.swCharles
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val l: LinearLayout = findViewById(R.id.ll_container)
        l.setOnTouchListener { _, _ ->
            l.isFocusable = true
            l.isFocusableInTouchMode = true
            l.requestFocus()
            false
        }
        initRadio()
        updateUI()
    }

    override fun onStart() {
        super.onStart()
        updateUI()
    }

    private fun initRadio() {
        swBproxy.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                hideKeyboard(swBproxy)
                swProxyMan.isChecked = false
                swCharles.isChecked = false
                openProxy(Type.BPROXY)
            } else {
                closeProxy()
            }
        }
        swProxyMan.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                hideKeyboard(swProxyMan)
                swBproxy.isChecked = false
                swCharles.isChecked = false
                openProxy(Type.PROXY_MAN)
            } else {
                closeProxy()
            }
        }
        swCharles.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                hideKeyboard(swCharles)
                swBproxy.isChecked = false
                swProxyMan.isChecked = false
                openProxy(Type.CHARLE)
            } else {
                closeProxy()
            }

        }
    }

    private fun updateUI() {
        val ipc = sp.getString(IP_CHARLES, "")?.let { Charles(it) }
        val ipp = sp.getString(IP_PROXY_MAN, "")?.let { ProxyMan(it) }
        val ipb = sp.getString(IP_BPROXY, "")?.let { BProxy(it) }

        val currentProxy = Proxy(System.getProperty("http.proxyHost").orEmpty(), System.getProperty("http.proxyPort").orEmpty())
        val str = if (currentProxy.host.isNotEmpty()) {
            if (listOf(ipc, ipp, ipb).any { it?.host == currentProxy.host && it.port == currentProxy.port }) {
                val ordinal = sp.getInt(PROXY_TYPE, -1)
                val type = ordinal.asType()
                when (type) {
                    Type.BPROXY -> {
                        swBproxy.isChecked = true
                        "当前代理为: <font color='#00FF00'>BmitmProxy"
                    }
                    Type.PROXY_MAN -> {
                        swProxyMan.isChecked = true
                        "当前代理为: <font color='#00FF00'>ProxyMan"
                    }
                    Type.CHARLE -> {
                        swCharles.isChecked = true
                        "当前代理为: <font color='#00FF00'>Charles"
                    }
                    else -> {
                        swCharles.isChecked = false
                        swProxyMan.isChecked = false
                        swBproxy.isChecked = false
                        "当前代理为: ${currentProxy.host}:${currentProxy.port}"
                    }
                }
            } else {
                swCharles.isChecked = false
                swProxyMan.isChecked = false
                swBproxy.isChecked = false
                "当前代理为: ${currentProxy.host}:${currentProxy.port}"
            }
        } else {
            swCharles.isChecked = false
            swProxyMan.isChecked = false
            swBproxy.isChecked = false
            "当前没有设置代理"
        }
        supportActionBar?.title = Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY)
        ipCharles.setText(ipc?.host.orEmpty())
        ipProxyMan.setText(ipp?.host.orEmpty())
        ipBproxy.setText(ipb?.host.orEmpty())
    }

    private fun openProxy(type: Type) {
        when {
            swBproxy.isChecked -> {
                openInternal(type, BProxy(ipBproxy.editableText.toString())) {
                    Toast.makeText(this@MainActivity, "请输入Bproxy的代理ip", Toast.LENGTH_SHORT).show()
                }
            }
            swProxyMan.isChecked -> {
                openInternal(type, ProxyMan(ipProxyMan.editableText.toString())) {
                    Toast.makeText(this@MainActivity, "请输入ProxyMan的代理ip", Toast.LENGTH_SHORT).show()
                }
            }
            swCharles.isChecked -> {
                openInternal(type, Charles(ipCharles.editableText.toString())) {
                    Toast.makeText(this@MainActivity, "请输入Charles的代理ip", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openInternal(type: Type, proxy: Proxy, whenEmpty: () -> Unit) {
        if (proxy.host.isEmpty()) {
            whenEmpty()
            return
        }
        if (!BaseAccessibilityService.checkAccessibilityEnabled(
                this, "com.zyk.wifiautoproxy.WifiProxyAccessibilityService"
            )
        ) {
            BaseAccessibilityService.goAccess(this)
        } else {
            save2Sp(type)
            WifiConfig.open = true
            WifiConfig.proxyType = proxy.javaClass.simpleName
            WifiConfig.host = proxy.host
            WifiConfig.port = proxy.port
            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }

    private fun save2Sp(type: Type) {
        sp.edit().putString(IP_BPROXY, ipBproxy.editableText.toString())
            .putString(IP_PROXY_MAN, ipProxyMan.editableText.toString())
            .putString(IP_CHARLES, ipCharles.editableText.toString())
            .putInt(PROXY_TYPE, type.ordinal)
            .apply()
    }

    private fun closeProxy() {
        if (!BaseAccessibilityService.checkAccessibilityEnabled(
                this, "com.zyk.wifiautoproxy.WifiProxyAccessibilityService"
            )
        ) {
            BaseAccessibilityService.goAccess(this)
        } else {
            WifiConfig.open = false
            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }

    override fun onDestroy() {
        // save2Sp()
        super.onDestroy()
    }

    companion object SpConst {
        const val IP_BPROXY = "ip_bmimtproxy"
        const val IP_PROXY_MAN = "ip_proxy_man"
        const val IP_CHARLES = "ip_charles"
        const val PROXY_TYPE = "proxy_type"
    }

    enum class Type {
        BPROXY, PROXY_MAN, CHARLE
    }

    private fun Int.asType(): Type? {
        return if (this < 0 || this >= Type.values().size) {
            null
        } else {
            Type.entries[this]
        }
    }
}