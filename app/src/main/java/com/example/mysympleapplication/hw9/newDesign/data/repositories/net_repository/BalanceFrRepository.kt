package com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository

import com.example.mysympleapplication.hw9.newDesign.domain.model.Balance

interface BalanceFrRepository {
    suspend fun saveBalanceFrStore(mail:String,balance: Balance)
    suspend fun getBalanceFrStore(mail: String):Balance?
}