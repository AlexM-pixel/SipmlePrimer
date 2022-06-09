package com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository

import com.example.mysympleapplication.hw9.newDesign.domain.model.Balance

interface BalanceDbRepository {
    suspend fun saveBalance(balance: Balance)
    suspend fun getBalance():Balance
}