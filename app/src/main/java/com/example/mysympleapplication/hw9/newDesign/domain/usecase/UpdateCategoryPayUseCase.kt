package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import android.util.Log
import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.ModelsSpendsDbRepository
import com.example.mysympleapplication.hw9.newDesign.domain.model.NameSpend
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import javax.inject.Inject

class UpdateCategoryPayUseCase @Inject constructor(private val repo: ModelsSpendsDbRepository) {
    operator fun invoke(nameSpend: NameSpend): Flow<Resource<Unit>> = flow {
            emit(Resource.Loading<Unit>())
            try {
                repo.updateNameSpend(nameSpend = nameSpend)
                emit(Resource.Success(Unit))
            } catch (e: Exception) {
                Log.e("UpdateCategoryPayUse", "ERROR update in DB: ${e.message}")
                emit(Resource.Error(message = e.message.toString()))
            }
        }
}