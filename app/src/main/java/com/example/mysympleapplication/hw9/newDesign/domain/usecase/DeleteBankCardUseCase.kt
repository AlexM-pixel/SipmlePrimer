package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.BankCardDbRepository
import javax.inject.Inject

class DeleteBankCardUseCase @Inject constructor(
    private val repository: BankCardDbRepository
) {
    suspend operator fun invoke(cardId: Long) {
        repository.deleteCard(cardId)
    }
}