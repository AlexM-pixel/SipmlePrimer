package com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository

import com.example.mysympleapplication.hw9.Spend
import com.example.mysympleapplication.hw9.SumSpendsOfMonth
import com.example.mysympleapplication.hw9.newDesign.data.mapper.SumSpendsOfMonthMapper
import com.example.mysympleapplication.hw9.newDesign.data.db.AppDataBase
import javax.inject.Inject

class SumMonthlySpendsRepositoryImpl @Inject constructor(private val db: AppDataBase, private val mapper: SumSpendsOfMonthMapper) : SumMonthlySpendsRepository {
    override fun getSumSpendsOfMonth(): List<SumSpendsOfMonth> {
        val listEntity = db.spendDao().getSumMonth()
        return mapper.fromEntityList(listEntity)
    }


    // менять обьект спенд не надо ури сохранять  надо
//    добавить вместо енамов новый ентити в базу с урлом
}