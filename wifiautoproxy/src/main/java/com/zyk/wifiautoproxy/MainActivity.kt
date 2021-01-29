package com.zyk.wifiautoproxy

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val sp by lazy { getSharedPreferences("wifi_auto_proxy", Context.MODE_PRIVATE) }
    private lateinit var ipCharles: EditText
    private lateinit var portCharles: RadioGroup
    private lateinit var ipHassan: EditText
    private lateinit var portHassan: RadioGroup

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
        portCharles = findViewById(R.id.port_charles)
        ipHassan = findViewById(R.id.ip_hassan)
        portHassan = findViewById(R.id.port_hassan)
        renderUI()
    }

    private fun renderUI() {
        supportActionBar?.title = "当前 IP 地址: ${IpGetUtils.getLocalIpAddress(this)}"
        ipCharles.setText(sp.getString(IP_CHARLES, ""))
        portCharles.check(sp.getInt(PORT_CHARLES, R.id.c_first))
        ipHassan.setText(sp.getString(IP_HASSAN, ""))
        portHassan.check(sp.getInt(PORT_HASSAN, R.id.h_second))
    }

    fun openCharles(view: View) {

        if (ipCharles.editableText.toString().isEmpty()) {
            Toast.makeText(this, "请输入Charles的代理ip", Toast.LENGTH_SHORT).show()
            return
        }
        if (!BaseAccessibilityService.checkAccessibilityEnabled(
                this,
                "com.zyk.wifiautoproxy.WifiProxyAccessibilityService"
            )
        ) {
            BaseAccessibilityService.goAccess(this)
        } else {
            save2Sp()
            WifiConfig.open = true
            WifiConfig.host = ipCharles.editableText.toString()
            WifiConfig.port =
                findViewById<RadioButton>(portCharles.checkedRadioButtonId).text.toString()
            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }

    fun openHassan(view: View) {
        if (ipHassan.editableText.toString().isEmpty()) {
            Toast.makeText(this, "请输入Hassan的代理ip", Toast.LENGTH_SHORT).show()
            return
        }
        if (!BaseAccessibilityService.checkAccessibilityEnabled(
                this,
                "com.zyk.wifiautoproxy.WifiProxyAccessibilityService"
            )
        ) {
            BaseAccessibilityService.goAccess(this)
        } else {
            save2Sp()
            WifiConfig.open = true
            WifiConfig.host = ipHassan.editableText.toString()
            WifiConfig.port =
                findViewById<RadioButton>(portHassan.checkedRadioButtonId).text.toString()
            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }

    private fun save2Sp() {
        sp.edit()
            .putString(IP_CHARLES, ipCharles.editableText.toString())
            .putInt(PORT_CHARLES, portCharles.checkedRadioButtonId)
            .putString(IP_HASSAN, ipHassan.editableText.toString())
            .putInt(PORT_HASSAN, portHassan.checkedRadioButtonId)
            .apply()
    }

    fun closeProxy(view: View) {
        if (!BaseAccessibilityService.checkAccessibilityEnabled(
                this,
                "com.zyk.wifiautoproxy.WifiProxyAccessibilityService"
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
        const val PORT_CHARLES = "port_charles"
        const val IP_HASSAN = "ip_hassan"
        const val PORT_HASSAN = "port_hassan"
    }

    override fun onDestroy() {
        // save2Sp()
        super.onDestroy()
    }
}