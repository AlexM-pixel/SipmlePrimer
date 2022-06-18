package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.SpendsDbRepository
import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetMonthlySpendsUseCase @Inject constructor(private val repo: SpendsDbRepository) {
    operator fun invoke(dateMonth: String): Flow<Resource<List<Spend>>> = flow {
        emit(Resource.Loading())
        try {
            val res = repo.getMonthSpends(dateMonth)
            emit(Resource.Success(res))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.message.toString()))
        }
    }
}