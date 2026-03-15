package com.example.mysympleapplication.hw9.newDesign.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.base.BaseFragment
import com.example.mysympleapplication.hw9.newDesign.di.builder.ViewModelFactory
import com.example.mysympleapplication.hw9.newDesign.domain.model.MonthStatDto
import com.example.mysympleapplication.hw9.newDesign.viewmodels.StatPlaceViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import javax.inject.Inject

class StatPlaceFragment : BaseFragment() {

    @Inject lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: StatPlaceViewModel by viewModels { viewModelFactory }

    // Переменные для View (или используйте Binding)
    private lateinit var tvTitle: TextView
    private lateinit var tvTotal: TextView
    private lateinit var chart: LineChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stat_place, container, false)
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Получаем имя места и год
        val placeName = arguments?.getString("arg_place_name") ?: return
        val year = arguments?.getString("arg_year") ?: "2026"

        // 2. Инициализация View
        val chartTitle= view.findViewById<TextView>(R.id.title_chart)
        tvTitle = view.findViewById(R.id.tv_place_title)
        tvTotal = view.findViewById(R.id.tv_total_spent_place)
        chart = view.findViewById(R.id.chart_place)
        val btnBack = view.findViewById<View>(R.id.btn_back)

        btnBack.setOnClickListener { findNavController().navigateUp() }


        // 3. Загружаем данные
        viewModel.loadData(placeName, year)

        // 4. Наблюдаем
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            Log.e("PlaceUiState.values","values: ${state}")
            chartTitle.text= "динамика за ${state.selectedYear} год"
            tvTitle.text = state.placeName
            tvTotal.text = "Всего: ${String.format("%.2f", state.totalSpent)} BYN"

            // Заполняем маленькие карточки
            setupSmallCard(view.findViewById(R.id.card_yearly), "Средний чек", "${String.format("%.2f",state.averageCheck)} BYN")
            setupSmallCard(view.findViewById(R.id.card_top_month), "Топ месяц", state.topMonth)
            setupSmallCard(view.findViewById(R.id.card_habits), "Ср. визитов", "${state.avgVisitsPerMonth} в мес.")
            setupSmallCard(view.findViewById(R.id.card_best_day), "Любимый день", state.bestDayOfWeek)
            setupSmallCard(view.findViewById(R.id.card_visits),"Всего покупок",state.visitsText)

            // Строим график
            setupChart(state.chartData)
        }
    }

    private fun setupSmallCard(cardView: View, title: String, value: String) {
        cardView.findViewById<TextView>(R.id.tv_title).text = title
        cardView.findViewById<TextView>(R.id.tv_value).text = value
    }

    private fun setupChart(data: List<MonthStatDto>) {
        if (data.isEmpty()) return

        val entries = data.mapIndexed { index, dto ->
            Entry(index.toFloat(), dto.total.toFloat())
        }
        val set = LineDataSet(entries, "").apply {
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            color = android.graphics.Color.parseColor("#6FCF97")
            fillColor = android.graphics.Color.parseColor("#6FCF97")
            fillAlpha = 50
            setDrawValues(false)
            setDrawCircles(true)
        }

        chart.apply {
            // ... настройки ...

            // ВАЖНО: Ось X теперь жестко от 0 до 11 (Янв - Дек)
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(
                    listOf("Янв", "Фев", "Мар", "Апр", "Май", "Июн", "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек")
                )
                position = XAxis.XAxisPosition.BOTTOM

                // >>> ВОТ ЭТА СТРОЧКА УБИРАЕТ ВЕРТИКАЛЬНЫЕ ПОЛОСЫ <<<
              this.setDrawGridLines(false)
                axisMinimum = 0f
                axisMaximum = 11f
                //labelCount = 12 // Показать все месяцы (или 6, чтобы не теснились)
            }
            axisLeft.apply {
                // Горизонтальные полосы можно оставить (они полезны) или тоже убрать:
                setDrawGridLines(false) // Раскомментируйте, если хотите совсем чистый график

                // Делаем горизонтальные линии пунктирными (как на макетах)
               // enableGridDashedLine(10f, 10f, 0f)

               // axisMinimum = 0f // График начинается с 0
            }

            this.data = LineData(set)
           // this.xAxis.
            this.legend.isEnabled = false
            this.description.isEnabled = false
            this.axisRight.isEnabled = false
            this.axisLeft.isEnabled = true
            invalidate()
        }
   }
}