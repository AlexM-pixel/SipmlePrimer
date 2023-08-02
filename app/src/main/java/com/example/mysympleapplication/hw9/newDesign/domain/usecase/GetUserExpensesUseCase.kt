package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import com.example.mysympleapplication.hw9.SumSpendsOfMonth
import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.SumSpendsRepository
import com.example.mysympleapplication.hw9.newDesign.domain.model.NameSpend
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetUserExpensesUseCase @Inject constructor(private val repo: SumSpendsRepository) {
    operator fun invoke(monthDate: String): Flow<Resource<SumSpendsOfMonth>> = flow {
        emit(Resource.Loading())
        try {
            val response = repo.getCurrentMonthExp(monthDate)
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.toString()))
        }
    }
}