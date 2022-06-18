package com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository

import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import com.example.mysympleapplication.hw9.newDesign.data.db.AppDataBase
import com.example.mysympleapplication.hw9.newDesign.data.mapper.SpendsMapper
import javax.inject.Inject

class SpendsDbRepositoryImpl @Inject constructor(
    private val db: AppDataBase,
    private val mapper: SpendsMapper
) : SpendsDbRepository {

    override suspend fun saveSpend(spend: Spend) {
        db.spendDao().insert(mapper.mapToEntity(spend))
    }

    override suspend fun getMonthSpends(monthDate: String): List<Spend> {
        return mapper.fromEntityList(db.spendDao().getMonthSpends(monthDate))
    }

    override suspend fun getDetailSpendsByName(name: String, date: String): List<Spend> {
        return mapper.fromEntityList(db.spendDao().getDetailSpendsByName(name = name, choiceDate = date))
    }

}