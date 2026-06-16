package com.example.mysympleapplication.hw9.newDesign.ui

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.base.BaseFragment
import com.example.mysympleapplication.hw9.newDesign.di.builder.ViewModelFactory
import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import com.example.mysympleapplication.hw9.newDesign.ui.adapters.MonthlySpendRvAdapter
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs
import com.example.mysympleapplication.hw9.newDesign.viewmodels.MonthlySpendsViewModel
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

const val ARG_DATE = "date_arg"


class MonthlySpendsFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    val viewModel: MonthlySpendsViewModel by viewModels { viewModelFactory }

    private lateinit var myAdapter: MonthlySpendRvAdapter
    private var date: String? = null
    private var totalMonthSum: Float = 0f

    // Оригинальный список из БД для работы поиска
    private var originalSpendsList: List<Spend> = emptyList()

    // Вьюшки из нового макета
    private lateinit var tvMonthTitle: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvPurchasesCount: TextView
    private lateinit var pbMonthLimit: LinearProgressIndicator
    private lateinit var tvLimitStatus: TextView
    private lateinit var etSearch: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            date = it.getString(ARG_DATE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Убедись, что в этот XML ты вставил тот красивый код макета с поиском и шапкой
        return inflater.inflate(R.layout.fragment_spends_of_mounth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupListeners()
        formatAndSetTitle()

        // Запрашиваем данные у ViewModel
        date?.let { viewModel.getMonthlySpends(it) }

        // Слушаем ответ
        viewModel.monthlySpendsLiveData.observe(viewLifecycleOwner) { list ->
            originalSpendsList = list ?: emptyList()
            myAdapter.setList(originalSpendsList) // Отдаем в адаптер

            updateSummaryCards(originalSpendsList) // Считаем итоги и лимиты в шапке
        }
    }

    private fun initViews(view: View) {
        tvMonthTitle = view.findViewById(R.id.tv_month_title)
        tvTotalAmount = view.findViewById(R.id.tv_total_amount)
        tvPurchasesCount = view.findViewById(R.id.tv_purchases_count)
        pbMonthLimit = view.findViewById(R.id.pb_month_limit)
        tvLimitStatus = view.findViewById(R.id.tv_limit_status)
        etSearch = view.findViewById(R.id.et_search)

        // Подключаем RecyclerView (ID из нового макета - rv_spends_detail)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_spends_detail)
        myAdapter = MonthlySpendRvAdapter()

        recyclerView.apply {
            adapter = myAdapter
            layoutManager = LinearLayoutManager(context)
        }

        // Твоя навигация при клике на элемент списка
        myAdapter.onItemClick = { name, dateSpend ->
            val bundle = Bundle()
            bundle.putString(ARG_NAME_DETAIL, name)
            bundle.putString(ARG_DATE_DETAIL, dateSpend)
            bundle.putFloat(ARG_TOTAL_VALUE, totalMonthSum)
            findNavController().navigate(
                R.id.action_spendsOfMonthFragment_to_detailMonthlyFragment,
                bundle
            )
        }
    }

    private fun setupListeners() {
        // Кнопка НАЗАД
        requireView().findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            findNavController().navigateUp()
        }

        // ==========================================
        // МАГИЯ ПОИСКА (ФИЛЬТРАЦИЯ "НА ЛЕТУ")
        // ==========================================
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.isEmpty()) {
                    myAdapter.setList(originalSpendsList) // Показываем все
                } else {
                    // Ищем совпадения в названии места (ignoreCase = true игнорирует большие/маленькие буквы)
                    val filteredList = originalSpendsList.filter { spend ->
                        spend.spendName.contains(query, ignoreCase = true)
                    }
                    myAdapter.setList(filteredList)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    // ==========================================
    // ЛОГИКА ШАПКИ (Бюджет и Итоги)
    // ==========================================
    private fun updateSummaryCards(list: List<Spend>) {
        // 1. Считаем итоги
        val totalSum = list.sumOf { it.value.toDoubleOrNull() ?: 0.0 }
        totalMonthSum = totalSum.toFloat()
        val itemsCount = list.size

        // Заполняем цифры
        tvTotalAmount.text = String.format(Locale.US, "%.2f", totalSum)
        tvPurchasesCount.text = itemsCount.toString()

        // 2. Логика прогресс-бара лимита
        val manualLimit = MainPrefs.monthlyLimit.toDouble()

        if (manualLimit > 0) {
            pbMonthLimit.visibility = View.VISIBLE
            tvLimitStatus.visibility = View.VISIBLE

            val percent = ((totalSum / manualLimit) * 100).toInt()
            pbMonthLimit.progress = percent.coerceIn(0, 100)

            if (percent > 100) {
                // Перерасход
                pbMonthLimit.setIndicatorColor(Color.parseColor("#FF5252")) // Красный
                tvLimitStatus.text = "Лимит превышен на ${percent - 100}%"
                tvLimitStatus.setTextColor(Color.parseColor("#FF5252"))
            } else {
                // В норме
                pbMonthLimit.setIndicatorColor(Color.parseColor("#6FCF97")) // Зеленый
                val left = manualLimit - totalSum
                tvLimitStatus.text = "Остаток лимита: ${left.toInt()} BYN"
                tvLimitStatus.setTextColor(Color.parseColor("#888888"))
            }
        } else {
            // Если лимит не задан в настройках — скрываем прогресс-бар
            pbMonthLimit.visibility = View.GONE
            tvLimitStatus.visibility = View.GONE
        }
    }

    // Вспомогательный метод для красивого заголовка ("Апрель 2026")
    private fun formatAndSetTitle() {
        val rawDate = date ?: return
        try {
            val sdfIn = SimpleDateFormat("MM-yyyy", Locale.US)
            val sdfOut = SimpleDateFormat("LLLL yyyy", Locale("ru"))
            val parsedDate = sdfIn.parse(rawDate)

            if (parsedDate != null) {
                // Делаем первую букву заглавной
                tvMonthTitle.text = sdfOut.format(parsedDate).replaceFirstChar { it.uppercase() }
            }
        } catch (e: Exception) {
            tvMonthTitle.text = rawDate
        }
    }
}