package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import com.example.mysympleapplication.hw9.SumSpendsOfMonth
import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.SpendsDataBaseRepository
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetMonthlyExpensesUseCase @Inject constructor(private val repo: SpendsDataBaseRepository) {
    operator fun invoke(): Flow<Resource<List<SumSpendsOfMonth>>> = flow {
        emit(Resource.Loading())
        try {
            val response = repo.getSumSpendsOfMonth()
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.toString()))
        }
    }
}