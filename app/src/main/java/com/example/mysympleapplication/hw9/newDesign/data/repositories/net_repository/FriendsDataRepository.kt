package com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository

import com.example.mysympleapplication.hw9.SumSpendsOfMonth
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.SpendEntity
import com.example.mysympleapplication.hw9.newDesign.domain.model.Balance
import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import com.example.mysympleapplication.hw9.newDesign.utils.Result

interface FriendsDataRepository {
    suspend fun getFriendsBalance(mail:String): Balance?
    suspend fun getFriendsExpensesByMonth(date: String): SumSpendsOfMonth
    suspend fun getTestFriendsExpensesByMonth(date: String,  mail: String): Result<Exception,List<Spend>>
}