package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import android.util.Log
import com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository.FireStoreRepository
import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import javax.inject.Inject

class SaveSpendFrStoreUseCase @Inject constructor(private val frRepository: FireStoreRepository) {
    operator fun invoke(spend: Spend): Flow<Resource<Unit>> =
        flow {
            emit(Resource.Loading<Unit>())
            try {
                frRepository.saveSpendFrStore(spend = spend, MainPrefs.mailUser)
                emit(Resource.Success(Unit))
            } catch (e: Exception) {
                Log.e("saveSpend", "ERROR save in Firestore: ${e.message}")
                emit(Resource.Error(message = e.message.toString()))
            }
        }
}