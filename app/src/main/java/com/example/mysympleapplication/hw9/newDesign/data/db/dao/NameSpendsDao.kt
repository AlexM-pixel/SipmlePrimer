package com.example.mysympleapplication.hw9.newDesign.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.FriendsSpendsEntity
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.NameSpendsEntity
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.SpendEntity

interface NameSpendsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNameSpend(nameSpends: NameSpendsEntity)

    @Query("SELECT * FROM name_spends")
    fun getAllNamesSpends(): List<NameSpendsEntity>

    @Insert
    suspend fun insertAllNamesSpends(namesList: List<NameSpendsEntity>)

    @Query("DELETE FROM name_spends WHERE id= :id")
    fun deleteNameSpend(id: Long?)
}