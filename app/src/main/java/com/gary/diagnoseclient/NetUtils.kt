package com.gary.diagnoseclient

import android.os.Handler
import android.os.Looper
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


class NetUtils {

    private val client = OkHttpClient()
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private var url: String? = null
    private var interval: Long = 3000
    private var callback: WebsiteStatusCallback? = null

    fun setCheckingParameter(url: String, interval: Long) {
        this.url = url
        this.interval = interval
    }

    fun startCheckingWebsiteStatus(callback: WebsiteStatusCallback) {
        val currentUrl = url ?: throw IllegalArgumentException("URL is not set.")

        stopCheckingWebsiteStatus()

        this.callback = callback
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
//                Log.i("NetUtils", "Checking...")
                checkWebsiteStatus(currentUrl)
                handler?.postDelayed(this, interval)
            }
        }
        handler?.post(runnable!!)
    }

    fun stopCheckingWebsiteStatus() {
        handler?.removeCallbacks(runnable!!)
        handler = null
        runnable = null
    }

    private fun checkWebsiteStatus(url: String) {
        try {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                throw IllegalArgumentException("URL must start with http:// or https://")
            }

            val request = Request.Builder().url(url).build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback?.onFailure(e.message ?: "Unknown error")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        callback?.onSuccess(response.code)
                    } else {
                        callback?.onFailure("Response code: ${response.code}")
                    }
                }
            })
        } catch (e: IllegalArgumentException) {
            // URL ERROR
            callback?.onFailure(e.message ?: "Unknown error")
        }
    }
}

interface WebsiteStatusCallback {
    fun onSuccess(responseCode: Int)
    fun onFailure(errorMessage: String)
}
