package com.example.mysympleapplication.hw9.newDesign.domain.model


data class PairSpendUiModel(
    val categoryName: String,
    val categoryIconUrl: String,

    val myName: String,
    val myAvatar: String,
    val myAmount: Float,

    val friendName: String,
    val friendAvatar: String,
    val friendAmount: Float,

    val totalAmount: Float
)
