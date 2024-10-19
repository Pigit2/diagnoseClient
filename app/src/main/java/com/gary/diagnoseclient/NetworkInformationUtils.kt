package com.gary.diagnoseclient

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.NetworkInterface

class NetworkInformationUtils(private val context: Context) {

    // 获取当前网络状态
    fun getNetworkInfo(): String {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo

        return if (activeNetwork != null && activeNetwork.isConnected) {
            "网络类型: ${activeNetwork.typeName}, 状态: 连接"
        } else {
            "网络状态: 断开"
        }
    }

    // 获取本地IP地址
    fun getLocalIpAddress(): String? {
//        ipv6
//        return try {
//            val interfaces = NetworkInterface.getNetworkInterfaces()
//            while (interfaces.hasMoreElements()) {
//                val networkInterface = interfaces.nextElement()
//                val address = networkInterface.inetAddresses
//                while (address.hasMoreElements()) {
//                    val ipAddress = address.nextElement()
//                    if (!ipAddress.isLoopbackAddress && ipAddress is InetAddress) {
//                        return ipAddress.hostAddress
//                    }
//                }
//            }
//            null
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
        return try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                val address = networkInterface.inetAddresses
                while (address.hasMoreElements()) {
                    val ipAddress = address.nextElement()
                    if (!ipAddress.isLoopbackAddress && ipAddress is InetAddress && ipAddress.hostAddress.contains(".")) {
                        return ipAddress.hostAddress // 仅返回IPv4地址
                    }
                }
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // 获取DNS
    fun getDnsServers(): List<String> {
//        ipv6
//        val dnsList = mutableListOf<String>()
//        try {
//            val interfaces = NetworkInterface.getNetworkInterfaces()
//            while (interfaces.hasMoreElements()) {
//                val networkInterface = interfaces.nextElement()
//                val addrs = networkInterface.inetAddresses
//                while (addrs.hasMoreElements()) {
//                    val addr = addrs.nextElement()
//                    if (!addr.isLoopbackAddress && addr is InetAddress) {
//                        // 添加DNS服务器的逻辑
//                        // 这里可以根据具体的需求添加相应的逻辑
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return dnsList
//        2
//        val dnsList = mutableListOf<String>()
//        try {
//            val process = Runtime.getRuntime().exec("getprop net.dns1")
//            val reader = BufferedReader(InputStreamReader(process.inputStream))
//            var line: String?
//            while (reader.readLine().also { line = it } != null) {
//                if (line!!.isNotEmpty()) {
//                    dnsList.add(line!!)
//                }
//            }
//            reader.close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return dnsList
        val dnsList = mutableListOf<String>()
        try {
            // 获取多个 DNS 地址
            val process = Runtime.getRuntime().exec("getprop net.dns1")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                if (line!!.isNotEmpty() && line!!.contains(".")) {
                    dnsList.add(line!!)
                }
            }
            reader.close()

            // 继续获取其他 DNS 地址
            val process2 = Runtime.getRuntime().exec("getprop net.dns2")
            val reader2 = BufferedReader(InputStreamReader(process2.inputStream))
            while (reader2.readLine().also { line = it } != null) {
                if (line!!.isNotEmpty() && line!!.contains(".")) {
                    dnsList.add(line!!)
                }
            }
            reader2.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return dnsList
    }

    fun checkDNS(): String {
        val dnsResult = try {
            InetAddress.getByName("www.baidu.com")
            "DNS解析正常"
        } catch (e: Exception) {
            "DNS解析失败: ${e.message}"
        }
        return dnsResult
    }
}