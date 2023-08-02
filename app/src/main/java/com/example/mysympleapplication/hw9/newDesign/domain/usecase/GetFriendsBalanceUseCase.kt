package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import com.example.mysympleapplication.hw9.SumSpendsOfMonth
import com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository.FriendsDataRepository
import com.example.mysympleapplication.hw9.newDesign.domain.model.Balance
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetFriendsBalanceUseCase @Inject constructor(private val repo: FriendsDataRepository) {
    operator fun invoke(mail: String): Flow<Resource<Balance?>> = flow {
        emit(Resource.Loading())
        try {
            val response = repo.getFriendsBalance(mail)
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.toString()))
        }
    }
}