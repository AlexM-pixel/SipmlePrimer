package com.example.mysympleapplication.hw9.newDesign.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.SpendsDbRepository
import com.example.mysympleapplication.hw9.newDesign.domain.model.MonthStatDto
import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class StatPlaceViewModel @Inject constructor(
    private val repository: SpendsDbRepository
) : ViewModel() {

    // Состояние экрана (все данные в одном классе)
    data class PlaceUiState(
        val placeName: String = "",
        val selectedYear: String = "",
        val totalSpent: Double = 0.0,
        val averageCheck: Double = 0.0,
        val yearlySummary: Double = 0.0, // Сумма за этот год
        val topMonth: String = "-",
        val avgVisitsPerMonth: Int = 0,
        val visitsText: String = "-",    // Текст "3 раза"
        val bestDayOfWeek: String = "-",
        val maxPurchase: Double = 0.0,   // Для карточки "Макс. покупка"
        val chartData: List<MonthStatDto> = emptyList()

    )

    private val _uiState = MutableLiveData<PlaceUiState>()
    val uiState: LiveData<PlaceUiState> get() = _uiState

     fun loadData(placeName: String,year: String) {
         repository.getSpendsByPlace(placeName).onEach { allSpends ->
             calculateStats(placeName, year, allSpends)
         }.launchIn(viewModelScope)
    }

    private fun calculateStats(name: String, year: String, allList: List<Spend>) {
        if (allList.isEmpty()) return

        // 1. ФИЛЬТРАЦИЯ ПО ГОДУ
        // Оставляем только те траты, которые относятся к выбранному году
        val listForYear = allList.filter {
            // date обычно в формате "yyyy-MM-dd"
            it.date.startsWith(year)
        }

        // Если в этом году трат не было - показываем нули
        if (listForYear.isEmpty()) {
            _uiState.value = PlaceUiState(placeName = name, selectedYear = year)
            return
        }

        // 2. БАЗОВЫЕ МЕТРИКИ
        val total = listForYear.sumOf { it.value.toDoubleOrNull() ?: 0.0 }
        val avgCheck = total / listForYear.size

        // Максимальная покупка (вместо пустой карточки Title)
        val maxPurchase = listForYear.maxOfOrNull { it.value.toDoubleOrNull() ?: 0.0 } ?: 0.0

        // 3. ТОП МЕСЯЦ
        val monthFormat = SimpleDateFormat("LLLL", Locale("ru")) // "Январь"
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        // Группируем по месяцам
        val spendsByMonth = listForYear.groupBy {
            val date = dateFormat.parse(it.date) ?: Date()
            monthFormat.format(date)
        }

        // Находим месяц с максимальной суммой
        val topMonthEntry = spendsByMonth.maxByOrNull { entry ->
            entry.value.sumOf { it.value.toDoubleOrNull() ?: 0.0 }
        }
        val topMonthStr = topMonthEntry?.key?.capitalize() ?: "-"

        // 4. ВИЗИТЫ (Сколько раз ходили)
        // Логика: просто количество операций за год
        val visitsText = "${listForYear.size} за год"
        // 4. Среднее кол-во визитов
        val monthsCount = spendsByMonth.keys.size
        val avgVisits = if (monthsCount > 0) listForYear.size / monthsCount else 0

        // 5. ЛЮБИМЫЙ ДЕНЬ НЕДЕЛИ
        val dayFormat = SimpleDateFormat("EEEE", Locale("ru"))
        val topDay = listForYear.groupBy {
            val date = dateFormat.parse(it.date) ?: Date()
            dayFormat.format(date)
        }.maxByOrNull { it.value.size }?.key?.capitalize() ?: "-"

        // 6. ГРАФИК (Строго 12 месяцев выбранного года)
        val chartStats = ArrayList<MonthStatDto>()
        val monthNumberFormat = SimpleDateFormat("MM", Locale.US) // "01", "02"

        // Создаем карту: "01" -> 0.0, "02" -> 0.0 ... "12" -> 0.0
        val monthlyTotals = MutableList(12) { 0.0 }

        listForYear.forEach { spend ->
            val date = dateFormat.parse(spend.date)
            if (date != null) {
                // Calendar.MONTH возвращает 0..11
                val cal = Calendar.getInstance().apply { time = date }
                val monthIndex = cal.get(Calendar.MONTH)
                val value = spend.value.toDoubleOrNull() ?: 0.0
                monthlyTotals[monthIndex] += value
            }
        }

        // Превращаем в DTO для графика
        for (i in 0..11) {
            // monthId должен быть "1", "2" и т.д. для LineChart форматтера
            chartStats.add(MonthStatDto((i + 1).toString(), monthlyTotals[i]))
        }

        _uiState.value = PlaceUiState(
            placeName = name,
            selectedYear = year,
            totalSpent = total,
            averageCheck = avgCheck,
            yearlySummary = total, // В контексте фильтра Total и Yearly совпадают
            topMonth = topMonthStr,
            avgVisitsPerMonth = avgVisits,
            visitsText = visitsText, // Новое поле (String) просто количество операций за год
            bestDayOfWeek = topDay,
            maxPurchase = maxPurchase,
            chartData = chartStats
        )
    }

    private fun String.capitalize() = replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}