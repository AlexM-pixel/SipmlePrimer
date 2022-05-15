package com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository

import com.example.mysympleapplication.hw9.SumSpendsOfMonth
import com.example.mysympleapplication.hw9.newDesign.data.mapper.SumSpendsOfMonthMapper
import com.example.mysympleapplication.hw9.newDesign.data.db.AppDataBase
import javax.inject.Inject

class SpendsDataBaseRepositoryImpl @Inject constructor(private val db: AppDataBase, private val mapper: SumSpendsOfMonthMapper) : SpendsDataBaseRepository {
    override fun getSumSpendsOfMonth(): List<SumSpendsOfMonth> {
        val listEntity = db.spendDao().getSumMonth()
        return mapper.fromEntityList(listEntity)
    }
}