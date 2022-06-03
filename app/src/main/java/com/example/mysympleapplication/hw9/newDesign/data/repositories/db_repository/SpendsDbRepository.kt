package com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository

import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend

interface SpendsDbRepository {
   suspend fun saveSpend(spend: Spend)
   suspend fun getMonthSpends(monthDate:String):List<Spend>
}