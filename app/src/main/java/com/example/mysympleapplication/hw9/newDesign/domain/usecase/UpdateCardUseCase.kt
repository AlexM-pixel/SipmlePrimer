package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.BankCardDbRepository
import com.example.mysympleapplication.hw9.newDesign.domain.model.BankCard
import javax.inject.Inject

class UpdateCardUseCase @Inject constructor(
    private val repository: BankCardDbRepository
) {
    suspend operator fun invoke(card: BankCard) {
        repository.updateCardBalance(card)
    }
}