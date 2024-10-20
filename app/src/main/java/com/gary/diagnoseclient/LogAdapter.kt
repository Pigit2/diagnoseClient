package com.gary.diagnoseclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LogAdapter(private val logList: MutableList<LogEntry>) : RecyclerView.Adapter<LogAdapter.LogViewHolder>() {

    class LogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.log_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.log_item, parent, false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val logEntry = logList[position]
        holder.textView.text = "${logEntry.timestamp}: ${logEntry.message}"
    }

    override fun getItemCount() = logList.size

    fun addLogEntry(entry: LogEntry) {
        logList.add(entry)
        notifyItemInserted(logList.size - 1)
    }

    fun removeOldestEntry() {
        if (logList.isNotEmpty()) {
            logList.removeAt(0) // 移除最旧的条目
            notifyItemRemoved(0) // 通知适配器更新
        }
    }
}