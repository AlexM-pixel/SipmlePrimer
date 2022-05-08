package com.example.mysympleapplication.hw9.newDesign.data.db.db_sours

import android.util.Log
import com.example.mysympleapplication.hw9.newDesign.data.db.AppDataBase
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.BalanceEntity
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.SpendEntity
import com.example.mysympleapplication.hw9.newDesign.utils.Result
import kotlinx.coroutines.delay
import javax.inject.Inject

class UsersDbSours @Inject constructor(private val db: AppDataBase) {
    suspend fun getUsersDataWhileLogin(): List<SpendEntity> {
        var res: List<SpendEntity> = mutableListOf()
        try {
            res = db.spendDao().getAllSpends()
            return res
        } catch (e: Exception) {
            Log.e("loginUser", "error from db: ${e.message}")
        }
        return res
    }

    suspend fun insertAllSpendsWhileLogin(list: List<SpendEntity>) {
        db.spendDao().insertAllSpends(list)
    }

    suspend fun getBalance() =
        db.balanceDao().getBalanceSignIn()

    suspend fun insertBalance(balanceUser: BalanceEntity) {
        db.balanceDao().insertBalance(balanceUser)
    }

}