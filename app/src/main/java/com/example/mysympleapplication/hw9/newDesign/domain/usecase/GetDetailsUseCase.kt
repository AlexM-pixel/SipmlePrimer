package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.DetailsSpendDbRepository
import com.example.mysympleapplication.hw9.newDesign.domain.model.DetailsSpend
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetDetailsUseCase @Inject constructor(private val repo: DetailsSpendDbRepository) {
    operator fun invoke(): Flow<Resource<List<DetailsSpend>>> = flow {
        emit(Resource.Loading())
        try {
            val res = repo.getAllDetails()
            emit(Resource.Success(data = res))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.message.toString()))
        }
    }
}