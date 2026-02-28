package com.example.mysympleapplication.hw9.newDesign.domain.model


import androidx.room.ColumnInfo

data class MonthStatDto(
    @ColumnInfo(name = "month") val month: String, // "01", "02", ... "12"
    @ColumnInfo(name = "total") val total: Double
)