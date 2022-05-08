package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import com.example.mysympleapplication.hw9.newDesign.data.repositories.expenses_repository.UserDataRepository
import javax.inject.Inject

class CheckDataFireStoreAndDbUseCase @Inject constructor(private val checkDataRepo: UserDataRepository) {
    suspend operator fun invoke(email: String) {
        checkDataRepo.getExpensesWhileLogin(email = email)
        checkDataRepo.getBalanceWhileLogin(email = email)
    }

}