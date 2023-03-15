package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import android.util.Log
import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.SpendsDbRepository
import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import javax.inject.Inject

class SaveSpendDbUseCase @Inject constructor(private val dbRepository: SpendsDbRepository) {
    operator fun invoke(spend: Spend): Flow<Resource<Unit>> =
        flow {
           emit(Resource.Loading<Unit>())
            try {
                dbRepository.saveSpend(spend = spend)
                emit(Resource.Success(Unit))
            } catch (e: Exception) {
                Log.e("SaveSpendDbUseCase","ERROR save in DB: ${e.message}")
                emit(Resource.Error(message = e.message.toString()))
            }
        }
}