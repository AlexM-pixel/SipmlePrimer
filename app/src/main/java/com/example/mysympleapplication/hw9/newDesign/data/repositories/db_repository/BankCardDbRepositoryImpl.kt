package com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository

import com.example.mysympleapplication.hw9.newDesign.data.db.AppDataBase
import com.example.mysympleapplication.hw9.newDesign.data.db.dao.BankCardDao
import com.example.mysympleapplication.hw9.newDesign.data.mapper.BankCardMapper
import com.example.mysympleapplication.hw9.newDesign.domain.model.BankCard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BankCardRepositoryImpl @Inject constructor(
    private val db: AppDataBase,
    private val mapper: BankCardMapper
) : BankCardDbRepository {
    override suspend fun saveCard(card: BankCard) {
       db.bankCardDao().insertCard(mapper.mapToEntity(card))
    }

    override fun getCards(): Flow<List<BankCard>> {
        return db.bankCardDao().getAllCards().map { listEntities ->
            listEntities.map { mapper.mapFromEntity(it) }
        }
    }

    override suspend fun getCardByDigits(digits: String): BankCard? {
        val entity = db.bankCardDao().getCardByDigits(digits) ?: return null
        return mapper.mapFromEntity(entity) // Entity -> Domain
    }

    override suspend fun updateCardBalance(card: BankCard) {
        db.bankCardDao().updateCard(mapper.mapToEntity(card))
    }

}