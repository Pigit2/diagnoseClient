package com.gary.diagnoseclient

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities


class ConnectUtils {

    var noNetworkConnection : Boolean = false
    var wifiConnection : Boolean = false
    var mobileDataConnection : Boolean = false
    var ethernetConnection : Boolean = false
//    val otherConnection : Boolean = false

    fun getNetworkStatus(context: Context) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networks = connectivityManager.allNetworks
        for (network in networks) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            if (networkCapabilities != null) {
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    wifiConnection = true
                }
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    mobileDataConnection = true
                }
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    ethernetConnection = true
                }
            } else {
                noNetworkConnection = true
            }
        }
    }
}