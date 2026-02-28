package com.example.mysympleapplication.hw9.newDesign.domain.model

import androidx.room.ColumnInfo

data class PlaceStatDto(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "total") val total: Double
)