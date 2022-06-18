package com.example.mysympleapplication.hw9.newDesign.data.entity_model

import androidx.room.ColumnInfo

data class CalendarSpendsEntity(
    @ColumnInfo(name = "id")
    val id: String?,
    @ColumnInfo(name = "totalValue")
    val totalValue: String,
    @ColumnInfo(name = "date")
    val date: String,
    @ColumnInfo(name = "spendName")
    val spendName: String
)
