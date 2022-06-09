package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import android.util.Log
import com.example.mysympleapplication.hw9.NameSpends
import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.NameSpendsDbRepository
import com.example.mysympleapplication.hw9.newDesign.domain.model.NameSpend
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class InsertModelNameBySpendUseCase @Inject constructor(private val repo: NameSpendsDbRepository) {
//    suspend operator fun invoke(): Resource<List<NameSpend>> {
//        return try {
//            val res = repo.getAllNameSpends()
//            Log.e("GetNameSpendUseCase", "GetNameSpendUseCase lisNameSpends.size = ${res.size}")
//            Resource.Success(res)
//        } catch (e: Exception) {
//            Log.e("GetNameSpendUseCase", "GetNameSpendUseCase Error: ${e.message}")
//            Resource.Error(e.message.toString())
//        }
//    }
suspend fun addNewModelNameBySpend(nameSpend: NameSpend){
        repo.insertNameSpend(nameSpend)
    }
}