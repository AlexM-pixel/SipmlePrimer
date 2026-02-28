package com.example.mysympleapplication.hw9.newDesign.ui.adapters


import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.domain.model.MonthStatDto
import com.example.mysympleapplication.hw9.newDesign.domain.model.PlaceStatDto
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.util.*

class ChartsPagerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var pieDataList: List<PlaceStatDto> = emptyList()
    private var lineDataList: List<MonthStatDto> = emptyList()

    companion object {
        const val TYPE_PIE = 0
        const val TYPE_LINE = 1
    }

    // Методы для обновления данных из Фрагмента
    fun submitPieData(data: List<PlaceStatDto>) {
        this.pieDataList = data
        notifyItemChanged(TYPE_PIE)
    }

    fun submitLineData(data: List<MonthStatDto>) {
        this.lineDataList = data
        notifyItemChanged(TYPE_LINE)
    }

    override fun getItemCount(): Int = 2 // У нас всегда 2 графика

    override fun getItemViewType(position: Int): Int = position

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_PIE) {
            PieHolder(inflater.inflate(R.layout.item_chart_pie, parent, false))
        } else {
            LineHolder(inflater.inflate(R.layout.item_chart_line, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PieHolder) holder.bind(pieDataList)
        if (holder is LineHolder) holder.bind(lineDataList)
    }

    // --- 1. ViewHolder для ПОНЧИКА (PieChart) ---
    inner class PieHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val chart: PieChart = view.findViewById(R.id.pieChart)
        // Цвета как на макете
        private val colors = listOf(
            "#6FCF97", "#FFD740", "#FF5252", "#BA68C8", "#4FC3F7", "#90A4AE"
        ).map { Color.parseColor(it) }

        fun bind(data: List<PlaceStatDto>) {
            if (data.isEmpty()) {
                chart.clear()
                return
            }

            // Логика "ТОП-4 + Прочее"
            val totalSum = data.sumOf { it.total }
            val entries = ArrayList<PieEntry>()

            // Берем первые 4
            data.take(4).forEach {
                entries.add(PieEntry(it.total.toFloat(), "")) // Имя не пишем в график, чтобы было чисто
            }

            // Остальные суммируем в "Прочее"
            val otherSum = data.drop(4).sumOf { it.total }
            if (otherSum > 0) {
                entries.add(PieEntry(otherSum.toFloat(), ""))
            }

            val dataSet = PieDataSet(entries, "").apply {
                this.colors = this@PieHolder.colors
                sliceSpace = 3f // Белые полоски между кусками
                setDrawValues(true)
                valueTextColor = Color.WHITE
                valueTextSize = 12f
            }

            // Настройка внешнего вида Пончика
            chart.apply {
                chart.data = PieData(dataSet)
                description.isEnabled = false
                legend.isEnabled = false
                isRotationEnabled = false

                // Дырка в центре
                isDrawHoleEnabled = true
                holeRadius = 55f
                setHoleColor(Color.WHITE)

                // Текст в центре
                centerText = "${totalSum.toInt()}\nBYN"
                setCenterTextSize(22f)
                setCenterTextColor(Color.parseColor("#333333"))

                animateY(1000)
                invalidate()
            }
        }
    }

    // --- 2. ViewHolder для ЛИНЕЙНОГО (LineChart) ---
    inner class LineHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val chart: LineChart = view.findViewById(R.id.lineChart)

        fun bind(data: List<MonthStatDto>) {
            if (data.isEmpty()) {
                chart.clear()
                return
            }

            // Подготовка точек (x = номер месяца, y = сумма)
            val entries = ArrayList<Entry>()
            // MPAndroidChart требует, чтобы x шли по порядку.
            // Превращаем "01" -> 1f, "02" -> 2f
            data.forEach {
                val x = it.month.toFloatOrNull() ?: 0f
                entries.add(Entry(x, it.total.toFloat()))
            }

            val set = LineDataSet(entries, "Траты").apply {
                mode = LineDataSet.Mode.CUBIC_BEZIER // <-- Плавная волна
                cubicIntensity = 0.2f

                setDrawFilled(true) // Включить заливку
                setDrawCircles(true)

                color = Color.parseColor("#6FCF97") // Цвет линии
                setCircleColor(Color.parseColor("#6FCF97"))
                lineWidth = 2f
                circleRadius = 4f
                setDrawCircleHole(false)

                // Градиент заливки (нужен файл drawable/fade_green)
                // Если файла нет, будет просто прозрачный зеленый
                try {
                    fillDrawable = ContextCompat.getDrawable(itemView.context, R.drawable.fade_green)
                } catch (e: Exception) {
                    fillColor = Color.parseColor("#6FCF97")
                    fillAlpha = 50
                }

                setDrawValues(false) // Убираем цифры над точками
            }

            chart.apply {
                this.data = LineData(set)
                description.isEnabled = false
                legend.isEnabled = false
                axisRight.isEnabled = false // Убираем правую ось

                // Ось X (Месяцы)
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    textColor = Color.GRAY
                    granularity = 1f // Шаг = 1 месяц
                    valueFormatter = IndexAxisValueFormatter(
                        listOf("", "Янв", "Фев", "Мар", "Апр", "Май", "Июн", "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек")
                    )
                }

                // Ось Y (Слева)
                axisLeft.apply {
                    textColor = Color.GRAY
                    setDrawGridLines(true)
                    enableGridDashedLine(10f, 10f, 0f) // Пунктир
                    axisMinimum = 0f
                }

                animateX(1000)
                invalidate()
            }
        }
    }
}