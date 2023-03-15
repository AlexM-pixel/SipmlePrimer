package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.ModelsSpendsDbRepository
import com.example.mysympleapplication.hw9.newDesign.domain.model.NameSpend
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(private val repo: ModelsSpendsDbRepository) {
    operator fun invoke(): Flow<Resource<List<NameSpend>>> = flow {
        emit(Resource.Loading())
        try {
            val res = repo.getAllNameSpends()
            emit(Resource.Success(data = res))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.message.toString()))
        }
    }
}