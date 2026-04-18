package com.example.mysympleapplication.hw9.newDesign.data.mapper


import android.graphics.Color
import android.util.Log
import com.example.mysympleapplication.hw9.Months
import com.example.mysympleapplication.hw9.newDesign.domain.model.MonthUiModel
import com.example.mysympleapplication.hw9.newDesign.domain.model.SumSpendsOfMonth
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs
import javax.inject.Inject

class MonthUiMapper @Inject constructor() {

    fun map(rawList: List<SumSpendsOfMonth>): List<MonthUiModel> {
        val result = mutableListOf<MonthUiModel>()

        // 1. Считаем эталон (Лимит или Среднее) один раз
        val benchmark = calculateBenchmark(rawList)
        val manualLimit = MainPrefs.monthlyLimit.toDouble()
        rawList.forEachIndexed { index, item ->
            val spendValue = item.valueSpends.toDouble()
            val monthData = Months.getMonth(item.dateM)

            // Если benchmark посчитался (значит данных хватило или стоит лимит) — показываем.
            val showProgress = benchmark > 0

            var progressValue = 0
            var progressColor = Color.parseColor("#6FCF97") // Зеленый (Спокойный)
            var progressText = ""
            var progressTextColor = Color.parseColor("#808080") // Серый

            if (showProgress) {
                val percent = ((spendValue / benchmark) * 100).toInt()
                progressValue = percent.coerceIn(0, 100)

                // Если установлен жесткий лимит — строже. Если авто-аналитика — мягче.
                if (manualLimit > 0) {
                    // --- РЕЖИМ ЛИМИТА (Жесткий) ---
                    when {
                        percent > 100 -> {
                            progressColor = Color.parseColor("#FF5252") // Красный
                            progressTextColor = progressColor
                            progressText = "Лимит превышен на ${percent - 100}%"
                        }

                        percent >= 90 -> {
                            progressColor = Color.parseColor("#FFCA28") // Желтый
                            progressText = "$percent% от лимита"
                        }

                        else -> {
                            progressText = "$percent% от лимита"
                        }
                    }
                } else {
                    // --- РЕЖИМ АНАЛИТИКИ (Мягкий / Банковский) ---
                    when {
                        // Значительное превышение (> 145%)
                        percent > 155 -> {
                            // Не красный, а оранжевый/терракотовый. Банки редко используют чистый #FF0000
                            progressColor = Color.parseColor("#FF0000")
                            progressTextColor = progressColor
                            progressText = "Активирован режим Покупашечки на ${percent}%"
                        }
                        // "превышение" (от 120% до 145%)
                        percent in 120..145 -> {
                            progressColor =
                                Color.parseColor("#FF7043") // Не красный, а оранжевый/терракотовый
                            // Можно вообще сделать серым, чтобы не отвлекать
                            progressText = "Выше обычного на ${percent - 100}%"
                        }
                        percent in 100..120 -> {
                            progressColor =
                                Color.parseColor("#FFD54F") //  желтый
                            // Можно вообще сделать серым, чтобы не отвлекать
                            progressText = "Чуть превысили на ${percent - 100}%"
                        }
                        percent in 80..100 -> {
                            progressColor =
                                Color.parseColor("#E2FF3B") // Спокойный желтый или Серый
                            // Можно вообще сделать серым, чтобы не отвлекать
                            progressText = "В пределах нормы   ${percent }%"
                        }
                        // Экономия (< 80%)
                        else -> {
                            progressColor = Color.parseColor("#6FCF97") // Зеленый
                            progressText = "Ниже обычного на ${100 - percent}%"
                        }
                    }
                }
            }

            result.add(
                MonthUiModel(
                    dateM = item.dateM,
                    monthName = monthData.nameMonth,
                    year = Regex("(\\d{4})").find(item.dateM)?.value ?: item.dateM,
                    sumText = item.valueSpends.toString(),
                    firstLetter = if (monthData.nameMonth.isNotEmpty()) monthData.nameMonth[0].toString() else "",
                    isProgressVisible = showProgress,
                    progressValue = progressValue,
                    color = progressColor,
                    colorText = progressTextColor,
                    progressText = progressText
                )
            )
        }
        return result
    }

    private fun calculateBenchmark(list: List<SumSpendsOfMonth>): Double {
        Log.e("BENCHMARK", "list size = ${list.size}")
        val manualLimit = MainPrefs.monthlyLimit
        if (manualLimit > 0) return manualLimit.toDouble()

        // Берем историю (до 6 месяцев для точности, если есть)
        // drop(1) убирает текущий незавершенный месяц
        val pastMonths = list.drop(1).take(6)
        Log.e("BENCHMARK", "pastMonths size = ${pastMonths.size}")
        // Превращаем в список чисел
        val values = pastMonths.mapNotNull {
            it.valueSpends.toString().toDoubleOrNull()
        }.filter { it > 0.0 } // Игнорируем месяцы с 0
        Log.d("BENCHMARK", "values = $values")
        if (values.size < 2) return 0.0

        // СОРТИРУЕМ (Обязательно для медианы)
        val sortedValues = values.sorted()

        // Находим середину
        val middle = sortedValues.size / 2

        return if (sortedValues.size % 2 == 1) {
            // Если нечетное количество (1, 3, 5) — берем ровно средний элемент
            sortedValues[middle]
        } else {
            // Если четное (2, 4, 6) — берем среднее между двумя центральными (сглаживание)
            (sortedValues[middle - 1] + sortedValues[middle]) / 2.0
        }
    }
}