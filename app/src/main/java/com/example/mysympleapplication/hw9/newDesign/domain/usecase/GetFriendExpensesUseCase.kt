package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import com.example.mysympleapplication.hw9.SumSpendsOfMonth
import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.SumSpendsRepository
import com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository.FriendsDataRepository
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetFriendExpensesUseCase @Inject constructor(private val repo: FriendsDataRepository) {
    suspend operator fun invoke(data:String, main:String)=
        repo.getTestFriendsExpensesByMonth(date = data, mail = main)



}