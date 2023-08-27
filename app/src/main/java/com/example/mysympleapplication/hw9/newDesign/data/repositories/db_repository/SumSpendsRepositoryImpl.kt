package com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository

import com.example.mysympleapplication.hw9.SumSpendsOfMonth
import com.example.mysympleapplication.hw9.newDesign.data.mapper.SumSpendsOfMonthMapper
import com.example.mysympleapplication.hw9.newDesign.data.db.AppDataBase
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.SumSpendsOfMonthEntity
import javax.inject.Inject

class SumSpendsRepositoryImpl @Inject constructor(
    private val db: AppDataBase,
    private val mapper: SumSpendsOfMonthMapper
) : SumSpendsRepository {
    override suspend fun getSumSpendsOfMonth(): List<SumSpendsOfMonth> {
        val listEntity = db.spendDao().getSumMonth()
        return mapper.fromEntityList(listEntity)
    }

    override suspend fun getCurrentMonthExp(mDate:String): SumSpendsOfMonth {
        val monthExpenses = db.spendDao().getCurrentMonthExpenses(mDate)?: SumSpendsOfMonthEntity(mDate,0f)
        return mapper.mapFromEntity(monthExpenses)
    }
}