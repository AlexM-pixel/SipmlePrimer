package com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository

import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import com.example.mysympleapplication.hw9.newDesign.data.db.AppDataBase
import com.example.mysympleapplication.hw9.newDesign.data.mapper.SpendsMapper
import com.example.mysympleapplication.hw9.newDesign.domain.model.MonthStatDto
import com.example.mysympleapplication.hw9.newDesign.domain.model.PlaceStatDto
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject

class SpendsDbRepositoryImpl @Inject constructor(
    private val db: AppDataBase,
    private val mapper: SpendsMapper
) : SpendsDbRepository {

    override suspend fun saveSpend(spend: Spend) {
        db.spendDao().insert(mapper.mapToEntity(spend))
    }

    override suspend fun update(spend: Spend) {
        db.spendDao().update(spend = mapper.mapToEntity(spend))
    }

    override suspend fun getMonthSpends(monthDate: String): List<Spend> {
        return mapper.fromEntityList(db.spendDao().getMonthSpends(monthDate))
    }

    override suspend fun getDetailSpendsByName(name: String, date: String): Flow<List<Spend>> {
        return mapper.fromEntityListFlow(
            db.spendDao().getDetailSpendsByName(name = name, choiceDate = date)
        )
    }

    override suspend fun delSpend(id: String) {
        db.spendDao().delete(id = id)
    }

    override suspend fun getSpendById(id: String): Spend {
        return mapper.mapFromEntity(db.spendDao().getSpend(id))
    }

    override fun getPlaceStatsByMonth(monthYear: String): Flow<List<PlaceStatDto>> {
        return db.spendDao().getPlaceStatsByMonth(monthYear)
    }

    override fun getPlaceStatsByYear(year: String): Flow<List<PlaceStatDto>> {
        return db.spendDao().getPlaceStatsByYear(year)
    }

    override fun getYearStats(year: String): Flow<List<MonthStatDto>> {
        return db.spendDao().getYearStats(year = year)
    }

    override suspend fun getFirstTransactionYear(): Int {
        val yearStr = db.spendDao().getFirstTransactionYear()
        // Если база пустая, возвращаем текущий год (чтобы не улететь в 1970)
        return yearStr?.toIntOrNull() ?: Calendar.getInstance().get(Calendar.YEAR)
    }


}