package com.gary.diagnoseclient

import android.util.Log
import android.widget.ScrollView
import android.widget.TextView
import java.io.BufferedReader
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.InputStreamReader


class LogUtils(private val scrollView: ScrollView, private val logTextView: TextView) {

    private var isLogging = false
    private var isVisible = false
    private val file = File(logTextView.context.getExternalFilesDir(null), "logcat.txt")
    private var process: Process? = null
    private var reader: BufferedReader? = null

    fun startLogging() {
        if (isLogging) return
        isLogging = true

        Thread {
            try {
                process = ProcessBuilder()
                    .command("logcat", "*:V")
                    .redirectErrorStream(true)
                    .start()
                reader = BufferedReader(InputStreamReader(process?.inputStream))
                var line: String?
                while (isLogging) {
                    line = reader?.readLine()
                    if (line != null) {
                        saveLogToFile(line)
                        if (isVisible) {
                            logTextView.post {
                                if (logTextView.lineCount > 3000) {
                                    logTextView.text = logTextView.text.split("\n")
                                        .drop(logTextView.lineCount - 3000).joinToString("\n")
                                }
                                logTextView.append("$line\n")
                                scrollView.smoothScrollTo(0, logTextView.bottom)
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                Log.d("finally", "inRun")
                reader?.close()
                process?.destroy()
            }
        }.start()
    }

    fun stopLogging() {
        isLogging = false
//        reader?.close()
//        process?.destroy()
        // 可以考虑在此处添加逻辑以确保立即停止，比如中断线程
    }

    fun startVisible() {
        isVisible = true
    }

    fun stopVisible() {
        isVisible = false
    }

    private fun saveLogToFile(log: String) {
        try {
            val writer = FileWriter(file, true)
            writer.write("$log\n")
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
