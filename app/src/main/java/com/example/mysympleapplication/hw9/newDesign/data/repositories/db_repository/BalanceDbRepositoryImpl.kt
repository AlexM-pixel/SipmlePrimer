package com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository

import com.example.mysympleapplication.hw9.newDesign.data.db.AppDataBase
import com.example.mysympleapplication.hw9.newDesign.data.mapper.BalanceMapper
import com.example.mysympleapplication.hw9.newDesign.data.net.FirestoreSource
import com.example.mysympleapplication.hw9.newDesign.domain.model.Balance
import javax.inject.Inject

class BalanceDbRepositoryImpl @Inject constructor(
    private val db: AppDataBase,
    private val mapper: BalanceMapper,
) : BalanceDbRepository {
    override suspend fun saveBalance(balance: Balance) {
        db.balanceDao().insertBalance(mapper.mapToEntity(balance))
    }

    override suspend fun getBalance(): Balance {
        return mapper.mapFromEntity(db.balanceDao().getBalance())
    }


}