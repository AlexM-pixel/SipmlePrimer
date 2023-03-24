package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import android.util.Log
import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.DetailsSpendDbRepository
import com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository.DetailsSpendFrRepository
import com.example.mysympleapplication.hw9.newDesign.domain.model.DetailsSpend
import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import javax.inject.Inject

class InsertDetailsSpendUseCase @Inject constructor(
    private val repo: DetailsSpendDbRepository,
    private val repoFr: DetailsSpendFrRepository
) {
    operator fun invoke(details: DetailsSpend,mail:String): Flow<Resource<Unit>> =
        flow {
            emit(Resource.Loading<Unit>())
            try {
                repo.insertDetail(detailsSpend = details)
                repoFr.saveDetailsSpend(mail = mail, detailsSpend = details)
                emit(Resource.Success(Unit))
            } catch (e: Exception) {
                Log.e("InsertDetailsSpUseCase", "ERROR save in DB: ${e.message}")
                emit(Resource.Error(message = e.message.toString()))
            }
        }
}