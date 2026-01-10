package com.example.mysympleapplication.hw9.newDesign.data.entity_model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spends")
data class SpendEntity(
    @PrimaryKey
    val id: Long = 0,
    val spendName: String = "",
    val value: String = "",
    val date: String = "",
    @ColumnInfo(name = "card_id")
    val cardId: String = ""
)
