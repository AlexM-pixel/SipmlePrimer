package com.example.mysympleapplication.hw9.newDesign.data.entity_model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spends")
data class SpendEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var spendName: String = "",
    var value: String = "",
    var date: String = ""
)
