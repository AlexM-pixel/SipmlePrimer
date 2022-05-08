package com.example.mysympleapplication.hw9.newDesign.data.entity_model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

data class SumSpendsOfMonthEntity(
    @ColumnInfo(name = "dateM")
    val dateM: String,
    @ColumnInfo(name = "value_spends")
    val valueSpends: Float
)