package com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository

import com.example.mysympleapplication.hw9.Spend
import com.example.mysympleapplication.hw9.SumSpendsOfMonth

interface SumMonthlySpendsRepository {
 suspend fun  getSumSpendsOfMonth(): List<SumSpendsOfMonth>
}