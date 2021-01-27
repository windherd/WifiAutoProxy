package com.zyk.wifiautoproxy

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zyk.wifiautoproxy.Util.hideKeyboard

/**
 * Created by windherd on 2020/12/30.
 */
class MainActivity : AppCompatActivity() {
    private val sp by lazy { getSharedPreferences("wifi_auto_proxy", Context.MODE_PRIVATE) }
    private lateinit var ipCharles: EditText
    private lateinit var ipProxyMan: EditText
    private lateinit var ipHassan: EditText
    private lateinit var ipHassan2: EditText
    private lateinit var radioCharles: RadioButton
    private lateinit var radioProxyMan: RadioButton
    private lateinit var radioHassan: RadioButton
    private lateinit var radioHassan2: RadioButton

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val l: LinearLayout = findViewById(R.id.ll_container)
        l.setOnTouchListener { _, _ ->
            l.isFocusable = true
            l.isFocusableInTouchMode = true
            l.requestFocus()
            false
        }
        ipCharles = findViewById(R.id.ip_charles)
        ipProxyMan = findViewById(R.id.ip_proxy_man)
        ipHassan = findViewById(R.id.ip_hassan)
        ipHassan2 = findViewById(R.id.ip_hassan_2)
        radioCharles = findViewById(R.id.radio_charles)
        radioProxyMan = findViewById(R.id.radio_proxy_man)
        radioHassan = findViewById(R.id.radio_hassan)
        radioHassan2 = findViewById(R.id.radio_hassan_2)
        initRadio()
        updateUI()
    }

    override fun onStart() {
        super.onStart()
        updateUI()
    }

    private fun initRadio() {
        radioCharles.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                hideKeyboard(radioCharles)
                radioProxyMan.isChecked = false
                radioHassan.isChecked = false
                radioHassan2.isChecked = false
            }

        }
        radioProxyMan.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                hideKeyboard(radioProxyMan)
                radioCharles.isChecked = false
                radioHassan.isChecked = false
                radioHassan2.isChecked = false
            }
        }
        radioHassan.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                hideKeyboard(radioHassan)
                radioCharles.isChecked = false
                radioProxyMan.isChecked = false
                radioHassan2.isChecked = false
            }
        }
        radioHassan2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                hideKeyboard(radioHassan2)
                radioCharles.isChecked = false
                radioProxyMan.isChecked = false
                radioHassan.isChecked = false
            }
        }
    }

    private fun updateUI() {
        val ipc = sp.getString(IP_CHARLES, "")?.let { Charles(it) }
        val ipp = sp.getString(IP_PROXY_MAN, "")?.let { ProxyMan(it) }
        val iph = sp.getString(IP_HASSAN, "")?.let { Hassan(it) }
        val iph2 = sp.getString(IP_HASSAN2, "")?.let { Hassan(it) }
        val currentProxy =
            Proxy(System.getProperty("http.proxyHost").orEmpty(), System.getProperty("http.proxyPort").orEmpty())
        val str = if (!currentProxy.host.isNullOrEmpty()) {
            when (currentProxy) {
                ipc -> {
                    radioCharles.isChecked = true
                    "当前代理为: Charles"
                }

                ipp -> {
                    radioProxyMan.isChecked = true
                    "当前代理为: ProxyMan"
                }

                iph -> {
                    radioHassan.isChecked = true
                    "当前代理为: Hassan"
                }

                iph2 -> {
                    radioHassan2.isChecked = true
                    "当前代理为: Hassan2"
                }

                else -> {
                    "当前代理为: ${currentProxy.host}:${currentProxy.port}"
                }
            }
        } else {
            radioCharles.isChecked = false
            radioProxyMan.isChecked = false
            radioHassan.isChecked = false
            radioHassan2.isChecked = false
            "当前没有设置代理"
        }
        supportActionBar?.title = str
        ipCharles.setText(ipc?.host.orEmpty())
        ipProxyMan.setText(ipp?.host.orEmpty())
        ipHassan.setText(iph?.host.orEmpty())
        ipHassan2.setText(iph2?.host.orEmpty())
    }

    fun openProxy(view: View) {
        when {
            radioCharles.isChecked -> {
                openInternal(Charles(ipCharles.editableText.toString())) {
                    Toast.makeText(this@MainActivity, "请输入Charles的代理ip", Toast.LENGTH_SHORT).show()
                }
            }

            radioProxyMan.isChecked -> {
                openInternal(ProxyMan(ipProxyMan.editableText.toString())) {
                    Toast.makeText(this@MainActivity, "请输入ProxyMan的代理ip", Toast.LENGTH_SHORT).show()
                }
            }

            radioHassan.isChecked -> {
                openInternal(Hassan(ipHassan.editableText.toString())) {
                    Toast.makeText(this@MainActivity, "请输入Hassan的代理ip", Toast.LENGTH_SHORT).show()
                }
            }

            radioHassan2.isChecked -> {
                openInternal(Hassan(ipHassan2.editableText.toString())) {
                    Toast.makeText(this@MainActivity, "请输入Hassan2的代理ip", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openInternal(proxy: Proxy, whenEmpty: () -> Unit) {
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
            save2Sp()
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

    private fun save2Sp() {
        sp.edit().putString(IP_CHARLES, ipCharles.editableText.toString())
            .putString(IP_PROXY_MAN, ipProxyMan.editableText.toString())
            .putString(IP_HASSAN, ipHassan.editableText.toString())
            .putString(IP_HASSAN2, ipHassan2.editableText.toString()).apply()
    }

    fun closeProxy(view: View) {
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

    companion object SpConst {
        const val IP_CHARLES = "ip_charles"
        const val IP_PROXY_MAN = "ip_proxy_man"
        const val IP_HASSAN = "ip_hassan"
        const val IP_HASSAN2 = "ip_hassan_2"
    }

    override fun onDestroy() {
        // save2Sp()
        super.onDestroy()
    }
}