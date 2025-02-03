package com.example.timerangeapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.timerangeapp.R
import com.example.timerange.data.ResultEntry

class ResultAdapter(private var results: List<ResultEntry>) :
    RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {

    inner class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val resultTextView: TextView = itemView.findViewById(R.id.textViewResult)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_result, parent, false)
        return ResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val entry = results[position]
        holder.resultTextView.text = "開始: ${entry.startTime}時, 終了: ${entry.endTime}時, 判定時刻: ${entry.checkTime}時, 結果: ${if(entry.isIncluded) "含む" else "含まない"}"
    }

    override fun getItemCount(): Int = results.size

    fun updateResults(newResults: List<ResultEntry>) {
        results = newResults
        notifyDataSetChanged()
    }
}