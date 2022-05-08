package com.example.mysympleapplication.hw9.newDesign.data.entity_model

import androidx.room.ColumnInfo

data class FriendsSumValueEntity(
    @ColumnInfo(name = "dateM")
    val datM: String,
    @ColumnInfo(name = "value_spends")
    val valueSpends: Float
)
