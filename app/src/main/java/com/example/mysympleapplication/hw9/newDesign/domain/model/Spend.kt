package com.example.mysympleapplication.hw9.newDesign.domain.model

data class Spend (
    val id:Long,
    var  spendName: String,
    var value: String,
    var date: String,
    var url:String?
)