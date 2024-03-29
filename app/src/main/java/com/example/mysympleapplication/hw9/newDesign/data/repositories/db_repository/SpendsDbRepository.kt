package com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository

import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import kotlinx.coroutines.flow.Flow

interface SpendsDbRepository {
    suspend fun saveSpend(spend: Spend)
    suspend fun getMonthSpends(monthDate: String): List<Spend>
    suspend fun getDetailSpendsByName(name: String, date: String): Flow<List<Spend>>
    suspend fun delSpend(id: String)
    suspend fun getSpendById(id: String):Spend
}