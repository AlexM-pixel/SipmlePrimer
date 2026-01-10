package com.example.mysympleapplication.hw9.newDesign.data.entity_model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bank_cards")
data class BankCardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "card_name")
    val cardName: String,
    @ColumnInfo(name = "last_four_digits")
    val lastFourDigits: String,
    @ColumnInfo(name = "balance")
    val balance: String,
    @ColumnInfo(name = "currency")
    val currency: String
)
