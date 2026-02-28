package com.example.mysympleapplication.hw9.newDesign.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.SpendsDbRepository
import com.example.mysympleapplication.hw9.newDesign.domain.model.MonthStatDto
import com.example.mysympleapplication.hw9.newDesign.domain.model.PlaceStatDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class StatisticSoloViewModel @Inject constructor(
    private val repository: SpendsDbRepository // Или ваши UseCases
) : ViewModel() {

    // Текущая выбранная дата
    private val calendar = Calendar.getInstance()
    private val currentRealYear = calendar.get(Calendar.YEAR)

    // Переменная для хранения минимального года (по умолчанию — текущий)
    private var minDbYear = currentRealYear

    // --- Formatters (Создаем один раз для экономии ресурсов) ---
    private val yearFormatter = SimpleDateFormat("yyyy", Locale.US)

    // Для заголовка UI ("2026 год")
    private val titleFormatter = SimpleDateFormat("yyyy 'год'", Locale("ru"))
    val selectedYear: String
        get() = SimpleDateFormat("yyyy", Locale.US).format(calendar.time)

    // LiveData для кнопок
    private val _isNextButtonVisible = MutableLiveData<Boolean>()
    val isNextButtonVisible: LiveData<Boolean> get() = _isNextButtonVisible

    private val _isPrevButtonVisible = MutableLiveData<Boolean>()
    val isPrevButtonVisible: LiveData<Boolean> get() = _isPrevButtonVisible

    // Данные для UI
    private val _placeStats = MutableLiveData<List<PlaceStatDto>>()
    val placeStats: LiveData<List<PlaceStatDto>> get() = _placeStats

    private val _yearStats = MutableLiveData<List<MonthStatDto>>()
    val yearStats: LiveData<List<MonthStatDto>> get() = _yearStats

    private val _dateTitle = MutableLiveData<String>() // Заголовок " 2026"
    val dateTitle: LiveData<String> get() = _dateTitle

    // --- Jobs (Для отмены старых запросов) ---
    private var dataLoadingJob: Job? = null

    init {
        // 1. Сразу грузим данные за текущий год
        loadData()

        // 2. Асинхронно узнаем, какой самый старый год в базе (для кнопки "Назад")
        viewModelScope.launch(Dispatchers.IO) {
            val dbYear = repository.getFirstTransactionYear()
            // Переключаемся на Main поток только для обновления UI-логики
            withContext(Dispatchers.Main) {
                // Если база пустая, dbYear может быть null или текущим
                minDbYear = dbYear
                updateNavigationButtons() // Обновляем кнопки, когда узнали границы
            }
        }
    }

    // Кнопка "Влево"
    fun prevPeriod() {
        // Не пускаем, если уже стоим на минимальном году
        if (calendar.get(Calendar.YEAR) > minDbYear) {
            calendar.add(Calendar.YEAR, -1)
            loadData()
        }
    }

    // Кнопка "Вправо"
    fun nextPeriod() {
        if (calendar.get(Calendar.YEAR) < currentRealYear) {
            calendar.add(Calendar.YEAR, 1)
            loadData()
        }
    }

    private fun loadData() {
        // 1. Отменяем предыдущую подписку, если пользователь быстро щелкает кнопки
        dataLoadingJob?.cancel()

        // Обновляем заголовок
        _dateTitle.value = titleFormatter.format(calendar.time)

        // Для Линейного графика и пончика: "2026"
        val yearStr = yearFormatter.format(calendar.time) // not used

        // Обновляем видимость кнопок
        updateNavigationButtons()

        // 4. Запускаем новую загрузку (сохраняем Job)
        dataLoadingJob = viewModelScope.launch {
            // Запускаем два потока параллельно в рамках одной корутины

            // Пончик и список
            repository.getPlaceStatsByYear(yearStr)
                .onEach { _placeStats.value = it }
                .launchIn(this) // Привязываем к текущему scope (dataLoadingJob)

            // Линейный график
            repository.getYearStats(yearStr)
                .onEach { _yearStats.value = it }
                .launchIn(this)
        }
    }

    private fun updateNavigationButtons() {
        val selectedYear = calendar.get(Calendar.YEAR)

        // Кнопка ВПЕРЕД: видна, только если мы в прошлом
        _isNextButtonVisible.value = selectedYear < currentRealYear
        // Кнопка НАЗАД: видна, только если есть более старые данные
        _isPrevButtonVisible.value = selectedYear > minDbYear
    }
}