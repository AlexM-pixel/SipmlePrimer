package com.example.mysympleapplication.hw9.newDesign.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.base.BaseFragment
import com.example.mysympleapplication.hw9.newDesign.di.builder.ViewModelFactory
import com.example.mysympleapplication.hw9.newDesign.ui.adapters.ChartsPagerAdapter
import com.example.mysympleapplication.hw9.newDesign.ui.adapters.PlacesStatsAdapter
import com.example.mysympleapplication.hw9.newDesign.viewmodels.StatisticSoloViewModel
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import javax.inject.Inject


class StatisticSoloFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: StatisticSoloViewModel by viewModels { viewModelFactory }

    // Адаптеры
    private val chartsAdapter = ChartsPagerAdapter()
    private val placesAdapter = PlacesStatsAdapter { placeName -> openPlaceDetails(placeName) }

    // View
    private lateinit var tvDate: TextView
    private lateinit var btnNext: View
    private lateinit var btnPrev: View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_statistics_nd, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Инициализация View
        val vpCharts = view.findViewById<ViewPager2>(R.id.vp_charts)
        val dotsIndicator = view.findViewById<WormDotsIndicator>(R.id.dots_charts)
        tvDate = view.findViewById(R.id.tv_current_year)
        btnPrev = view.findViewById<View>(R.id.btn_prev_year) // или year, как назвали
        btnNext = view.findViewById<View>(R.id.btn_next_year)
        val rvList = view.findViewById<RecyclerView>(R.id.rv_stats_details)
        // 2. Настройка Адаптеров
        vpCharts.adapter = chartsAdapter
        dotsIndicator.attachTo(vpCharts)

        rvList.layoutManager = LinearLayoutManager(context)
        rvList.adapter = placesAdapter
        // Отключаем скролл у списка, чтобы работал общий скролл экрана
        // rvList.isNestedScrollingEnabled = false

        // 3. Клик-листенеры
        btnPrev.setOnClickListener { viewModel.prevPeriod() }
        btnNext.setOnClickListener { viewModel.nextPeriod() }

        // 4. Подписка на данные
        observeViewModel()
    }

    private fun observeViewModel() {
        // Заголовок даты
        viewModel.dateTitle.observe(viewLifecycleOwner) { title ->
            tvDate.text = title
        }

        // Данные по местам -> В Пончик (стр 1) и в Список (внизу)
        viewModel.placeStats.observe(viewLifecycleOwner) { list ->
            // Обновляем Пончик
            chartsAdapter.submitPieData(list)
            // Обновляем Список снизу
            placesAdapter.submitList(list)
        }

        // Данные по году -> В Линейный график (стр 2)
        viewModel.yearStats.observe(viewLifecycleOwner) { list ->
            chartsAdapter.submitLineData(list)
        }

        // Кнопка Вперед
        viewModel.isNextButtonVisible.observe(viewLifecycleOwner) { isVisible ->
            btnNext.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
            btnNext.isEnabled = isVisible
        }

        // Кнопка Назад
        viewModel.isPrevButtonVisible.observe(viewLifecycleOwner) { isVisible ->
            btnPrev.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
            btnPrev.isEnabled = isVisible
        }
    }

    private fun openPlaceDetails(placeName: String) {
        val year: String = viewModel.selectedYear// Берем текущий год из ViewModel

        // Вариант А: Переход на новый фрагмент через Navigation Component
        val bundle = Bundle().apply {
            putString("arg_place_name", placeName)
            putString("arg_year", year)
        }

        // Предполагаем, что у тебя есть фрагмент для деталей (например, DetailsFragment)
        // findNavController().navigate(R.id.action_statistics_to_details, bundle)
        // ИЛИ (для теста) просто покажем Тост
        showMessage("История $placeName за $year год")

    }
}