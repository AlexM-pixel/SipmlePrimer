package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import android.util.Log
import com.example.mysympleapplication.hw9.NameSpends
import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.NameSpendsDbRepository
import com.example.mysympleapplication.hw9.newDesign.domain.model.NameSpend
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetNameSpendsListUseCase @Inject constructor(private val repo: NameSpendsDbRepository) {
    operator fun invoke(): Flow<Resource<List<NameSpend>>> = flow {
        try {
            val res = repo.getAllNameSpends()
            Log.e("GetNameSpendUseCase", "GetNameSpendUseCase lisNameSpends.size = ${res.size}")
            emit(Resource.Success(res))
        } catch (e: Exception) {
            Log.e("GetNameSpendUseCase", "GetNameSpendUseCase Error: ${e.message}")
            emit(Resource.Error(e.message.toString()))
        }
    }
}