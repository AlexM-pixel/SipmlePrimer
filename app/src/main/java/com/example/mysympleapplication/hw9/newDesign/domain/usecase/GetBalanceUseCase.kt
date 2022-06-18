package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.BalanceDbRepository
import com.example.mysympleapplication.hw9.newDesign.domain.model.Balance
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetBalanceUseCase @Inject constructor(private val repoDb: BalanceDbRepository) {
    operator fun invoke(): Flow<Resource<Balance>> = flow {
        emit(Resource.Loading())
        try {
            val res = repoDb.getBalance()
            emit(Resource.Success(data = res))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.message.toString()))
        }

    }
}