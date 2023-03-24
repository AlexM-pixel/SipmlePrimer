package com.example.mysympleapplication.hw9.newDesign.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.FriendsSpendsEntity
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.NameSpendsEntity
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.SpendEntity

@Dao
interface NameSpendsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNameSpend(nameSpends: NameSpendsEntity)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateNameSpend(nameSpends: NameSpendsEntity)

    @Query("SELECT * FROM name_spends")
    suspend fun getAllNamesSpends(): List<NameSpendsEntity>

    @Query("SELECT * FROM name_spends")
    fun getAllNamesNoSuspendSpends(): List<NameSpendsEntity>

    @Insert
    suspend fun insertAllNamesSpends(namesList: List<NameSpendsEntity>)

    @Query("DELETE FROM name_spends WHERE id= :id")
    fun deleteNameSpend(id: Long?)
}