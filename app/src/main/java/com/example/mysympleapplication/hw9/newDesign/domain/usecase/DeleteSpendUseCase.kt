package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import android.util.Log
import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.SpendsDbRepository
import com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository.FireStoreRepository
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import javax.inject.Inject

class DeleteSpendUseCase @Inject constructor(
    private val repoDb: SpendsDbRepository,
    private val repoFr: FireStoreRepository
) {
    operator fun invoke(id: String, mail: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            repoDb.delSpend(id)
            repoFr.deleteSpendFrStore(id = id, mail = mail)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            Log.e("SaveSpendDbUseCase", "ERROR save in DB: ${e.message}")
            emit(Resource.Error(message = e.message.toString()))
        }
    }
}