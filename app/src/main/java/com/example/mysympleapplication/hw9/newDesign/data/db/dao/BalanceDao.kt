package com.example.mysympleapplication.hw9.newDesign.data.db.dao

import androidx.room.*
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.BalanceEntity

@Dao
interface BalanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBalance(balance: BalanceEntity)

    @Update
    fun updateB(balance: BalanceEntity)
    

    @Query("SELECT * FROM balance ")
    suspend fun getBalance(): BalanceEntity
}