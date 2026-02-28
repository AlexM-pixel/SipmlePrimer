package com.example.mysympleapplication.hw9.newDesign.ui.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.domain.model.PlaceStatDto
import java.util.Locale

class PlacesStatsAdapter(private val onItemClick: (placeName: String) -> Unit) :
    RecyclerView.Adapter<PlacesStatsAdapter.PlaceHolder>() {

    private var list: List<PlaceStatDto> = emptyList()

    // Цвета для иконок (как на графике)
    private val colors = listOf(
        "#6FCF97", "#FFD740", "#FF5252", "#BA68C8", "#4FC3F7", "#90A4AE"
    ).map { Color.parseColor(it) }

    fun submitList(newList: List<PlaceStatDto>) {
        list = newList
        Log.e("submitList", "list = ${newList}")
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceHolder {
        return PlaceHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_place_stat, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PlaceHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int = list.size

    inner class PlaceHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvName: TextView = view.findViewById(R.id.tv_place_name)
        private val tvAmount: TextView = view.findViewById(R.id.tv_amount)
        private val tvPercent: TextView = view.findViewById(R.id.tv_percent)
        private val progressBar: ProgressBar = view.findViewById(R.id.pb_stat)
        private val ivDot: View = view.findViewById(R.id.iv_dot) // Кружок
        private val tvLetter: TextView =
            view.findViewById(R.id.tv_place_letter) // Буква внутри кружка

        fun bind(item: PlaceStatDto, position: Int) {
            tvName.text = item.name
            tvAmount.text = "${String.format(Locale.US, "%.2f", item.total)} BYN"

            // 1. Считаем процент от ОБЩЕЙ суммы списка
            val totalSum = list.sumOf { it.total }
            val percent = if (totalSum > 0) ((item.total / totalSum) * 100).toInt() else 0
            tvPercent.text = "$percent%"
            progressBar.progress = percent

            // 2. Красим элементы в цвет (циклично по списку цветов)
            val color = colors[position % colors.size]

            // Красим прогресс-бар
            progressBar.progressTintList = ColorStateList.valueOf(color)

            // Красим кружок (фон)
            ivDot.backgroundTintList = ColorStateList.valueOf(color)

            // Ставим первую букву названия
            if (item.name.isNotEmpty()) {
                tvLetter.text = item.name.trim().first().uppercase()
            }
            itemView.setOnClickListener { onItemClick(item.name) }
        }
    }
}