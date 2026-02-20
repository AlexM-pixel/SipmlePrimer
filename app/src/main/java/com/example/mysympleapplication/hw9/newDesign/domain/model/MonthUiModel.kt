package com.example.mysympleapplication.hw9.newDesign.domain.model


import androidx.annotation.ColorInt

data class MonthUiModel(
    val dateM: String,       // Исходная дата для клика
    val monthName: String,   // "Сентябрь"
    val year: String,        // "2025"
    val sumText: String,     // "1 151.57"
    val firstLetter: String, // "С"

    // Поля для прогресс-бара (могут быть скрыты)
    val isProgressVisible: Boolean,
    val progressValue: Int,      // 0..100
    @ColorInt val color: Int,    // Цвет полоски
    @ColorInt val colorText: Int,    // Цвет   текста полоски
    val progressText: String     // "Выше обычного на 5%"
)