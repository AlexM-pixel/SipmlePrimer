package com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository

import com.example.mysympleapplication.hw9.newDesign.domain.model.Balance
import com.example.mysympleapplication.hw9.newDesign.domain.model.PartnerState
import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import com.example.mysympleapplication.hw9.newDesign.domain.model.SumSpendsOfMonth
import com.example.mysympleapplication.hw9.newDesign.utils.Result
import kotlinx.coroutines.flow.Flow

interface FriendsDataRepository {
    suspend fun getFriendsBalance(mail:String): Balance?
    suspend fun getFriendsExpensesByMonth(date: String): SumSpendsOfMonth
    suspend fun getTestFriendsExpensesByMonth(date: String,  mail: String): Result<Exception,List<Spend>>
    suspend fun getFriendProfile(mail: String): Result<Exception, Pair<String, String>>


    fun observePartnerState(myEmail: String): Flow<PartnerState>
    suspend fun respondToInvite(myEmail: String, friendEmail: String, isAccepted: Boolean): Result<Exception, Unit>
    suspend fun sendInvite(myEmail: String, friendEmail: String): Result<Exception, Unit>
}