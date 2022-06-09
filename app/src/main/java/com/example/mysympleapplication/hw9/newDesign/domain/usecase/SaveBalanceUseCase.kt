package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import android.util.Log
import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.BalanceDbRepository
import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.NameSpendsDbRepository
import com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository.BalanceFrRepository
import com.example.mysympleapplication.hw9.newDesign.domain.model.Balance
import javax.inject.Inject

class SaveBalanceUseCase @Inject constructor(
    private val repoDb: BalanceDbRepository,
    private val repoFr: BalanceFrRepository
) {
    suspend fun saveBalance(mail: String, balance: Balance) {
        try {
            repoDb.saveBalance(balance = balance)
            repoFr.saveBalanceFrStore(mail, balance)
        }catch (e:Exception){
            Log.e("SaveBalanceUseCase","ERROR saveBalance: ${e.message}")
        }
    }
}