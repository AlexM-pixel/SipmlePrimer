package com.example.mysympleapplication.hw9.newDesign.domain.model

data class Spend (
    val id:Long,
    val  spendName: String,
    val value: String,
    var date: String
){
}