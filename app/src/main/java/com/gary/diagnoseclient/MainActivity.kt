package com.gary.diagnoseclient

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gary.diagnoseclient.ForegroundService.Companion.CHANNEL_ID
import com.google.android.material.switchmaterial.SwitchMaterial

class MainActivity : AppCompatActivity() {
    private val netUtils = NetUtils()
    private val connectUtils = ConnectUtils()
    private lateinit var logUtils: LogUtils
    private lateinit var networkInformationUtils: NetworkInformationUtils
    private val url: String = "https://www.baidu.com"
    private val interval: Long = 3000
    private lateinit var adapter: LogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        createNotificationChannel()
//        Intent(this, ForegroundService::class.java).also { intent ->
//            startForegroundService(intent)
//        }

        val cellular: TextView = findViewById(R.id.cellular_text)
        val wlan: TextView = findViewById(R.id.wlan_text)
        val ethernet: TextView = findViewById(R.id.ethernet_text)
        val http1: TextView = findViewById(R.id.http1_text)
//        val logText: TextView = findViewById(R.id.log_text)
//        val scrollView: ScrollView = findViewById(R.id.scrollView2)
        val connection: TextView = findViewById(R.id.connection)
        val ip: TextView = findViewById(R.id.ip_text)
        val dns: TextView = findViewById(R.id.dns_text)
        val dnsResolver: TextView = findViewById(R.id.dnsResolver_text)
        val isVisible: SwitchMaterial = findViewById(R.id.logVisible_switch)
        val logText: RecyclerView = findViewById(R.id.recycler_view)


        connectUtils.getNetworkStatus(this)
        Log.d("noNetworkConnection", connectUtils.noNetworkConnection.toString())
        Log.d("wifiConnection", connectUtils.wifiConnection.toString())
        Log.d("mobileDataConnection", connectUtils.mobileDataConnection.toString())
        Log.d("ethernetConnection", connectUtils.ethernetConnection.toString())
        cellular.text = connectUtils.mobileDataConnection.toString()
        wlan.text = connectUtils.wifiConnection.toString()
        ethernet.text = connectUtils.ethernetConnection.toString()

        networkInformationUtils = NetworkInformationUtils(this)
        connection.text = networkInformationUtils.getNetworkInfo()
        ip.text = networkInformationUtils.getLocalIpAddress() ?: "未获取到IP地址"
        dns.text = networkInformationUtils.getDnsServers().joinToString(", ")
//        dnsResolver.text = networkInformationUtils.checkDNS()


        netUtils.setCheckingParameter(url, interval)
        netUtils.startCheckingWebsiteStatus(object : WebsiteStatusCallback {
            override fun onSuccess(responseCode: Int) {
                Log.d("WebsiteStatus", "Response is successful: $responseCode")



                runOnUiThread( Runnable {
                    http1.text = getString(R.string.response, responseCode.toString())
                })

                netUtils.stopCheckingWebsiteStatus()

            }

            override fun onFailure(errorMessage: String) {
                Log.e("WebsiteStatus", "Error: $errorMessage")

                runOnUiThread( Runnable {
                    http1.text = errorMessage
                })
            }

        })


        adapter = LogAdapter(mutableListOf())
        logText.adapter = adapter
        logText.layoutManager = LinearLayoutManager(this)

        logUtils = LogUtils(logText, adapter)

        logUtils.startLogging()
        isVisible.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                logUtils.startVisible()
            else
                logUtils.stopVisible()
            Log.d("isChecked", isChecked.toString())
        }


        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                Log.i("定时日志", "这是一句定时输出的日志")
                handler.postDelayed(this, 10) // 每秒输出一次
            }
        }
        handler.post(runnable) // 开始定时任务

        Handler(Looper.getMainLooper()).postDelayed({
            isVisible.isEnabled = true // 5 秒后启用按钮
        }, 10000) // 5000 毫秒

    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Network Monitor Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        super.onDestroy()
        logUtils.stopLogging()
    }

}