package com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository

import com.example.mysympleapplication.hw9.newDesign.domain.model.BankCard
import kotlinx.coroutines.flow.Flow

interface BankCardDbRepository {
    suspend fun saveCard(card: BankCard)
    fun getCards(): Flow<List<BankCard>>
    suspend fun getCardByDigits(digits: String): BankCard?
    suspend fun updateCardBalance(card: BankCard)
    suspend fun deleteCard(cardId: Long)
}