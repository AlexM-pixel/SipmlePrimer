package com.example.mysympleapplication.hw9.newDesign.domain.model

data class BankCard(
    val id: Long,
    val cardName: String,       // Например: "Зарплатная"
    val lastFourDigits: String, // "1646" (для поиска по СМС)
    val balance: String,
    val currency: String = "BYN"
)
