package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository.FriendsDataRepository
import javax.inject.Inject

class GetFriendExpensesUseCase @Inject constructor(private val repo: FriendsDataRepository) {
    suspend operator fun invoke(data:String, main:String)=
        repo.getTestFriendsExpensesByMonth(date = data, mail = main)



}