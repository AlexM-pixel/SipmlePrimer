package com.example.mysympleapplication.hw9.newDesign.data.repositories.expenses_repository

interface UserDataRepository {
    suspend fun getExpensesWhileLogin(email: String)
    suspend fun getBalanceWhileLogin(email: String)
}