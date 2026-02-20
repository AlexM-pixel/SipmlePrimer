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
import com.example.mysympleapplication.hw9.Months
import com.example.mysympleapplication.hw9.SumSpendsOfMonth
import com.example.mysympleapplication.hw9.newDesign.domain.model.MonthUiModel
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs
import com.google.android.material.progressindicator.LinearProgressIndicator

class SumMonthSpendsRvAdapter : RecyclerView.Adapter<SumMonthSpendsRvAdapter.MyHolder>() {
    private var uIList: List<MonthUiModel> = emptyList()
    lateinit var onItemClick: (String) -> Unit

    // Переменная для хранения "Нормы" (Лимит или Среднее)
    private var benchmarkAmount: Double = 0.0
    private var isManualMode: Boolean = false

    fun setMonthList(list: List<MonthUiModel>) {
        this.uIList = list
        // 1. Вычисляем "Эталон" один раз при загрузке данных
        notifyDataSetChanged()
    }


    inner class MyHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val titleTextView: TextView = view.findViewById(R.id.home_title_spend_nd)
        private val dateTextView: TextView = view.findViewById(R.id.home_date_spends_nd)
        private val valueTextView: TextView = view.findViewById(R.id.home_value_spends_nd)
        private val imageTextView: TextView = view.findViewById(R.id.image_text_spends_nd)

        // Поля прогресса (из нового блока LinearLayout)
        private val progressLayout: View = view.findViewById(R.id.layout_progress)
        private val progressBar: LinearProgressIndicator =
            view.findViewById(R.id.progress_horizontalBar)
        private val progressText: TextView = view.findViewById(R.id.text_for_progressBar)


        fun onBind(item: MonthUiModel) {
            // Текст и картинки
            imageTextView.text = item.firstLetter
            titleTextView.text = item.monthName
            dateTextView.text = item.year
            valueTextView.text = item.sumText

            // Прогресс бар
            if (item.isProgressVisible) {
                progressLayout.visibility = View.VISIBLE
                progressBar.progress = item.progressValue
                progressBar.setIndicatorColor(item.color) // Устанавливаем готовый цвет
                progressText.text = item.progressText
                progressText.setTextColor(item.colorText)
            } else {
              //  progressLayout.visibility = View.GONE // можно скрыть поа больше трех месяцев не наберется
            }

            itemView.setOnClickListener { onItemClick.invoke(item.dateM) }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_spends, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
       holder.onBind(uIList[position])
    }

    override fun getItemCount(): Int {
        return uIList.size
    }
}