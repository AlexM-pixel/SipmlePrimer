package com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository

import com.example.mysympleapplication.hw9.Spend
import com.example.mysympleapplication.hw9.SumSpendsOfMonth

interface SumSpendsRepository {
 suspend fun  getSumSpendsOfMonth(): List<SumSpendsOfMonth>
 suspend fun getCurrentMonthExp(mDate:String): SumSpendsOfMonth
}