package com.example.mysympleapplication.hw9.newDesign.domain.usecase


import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.BankCardDbRepository
import com.example.mysympleapplication.hw9.newDesign.domain.model.BankCard
import javax.inject.Inject

class GetCardByDigitsUseCase @Inject constructor(
    private val repository: BankCardDbRepository
) {
    // Возвращает null, если карта не найдена (например, новая карта)
    suspend operator fun invoke(digits: String): BankCard? {
        return repository.getCardByDigits(digits)
    }
}