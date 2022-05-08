package com.example.mysympleapplication.hw9.newDesign.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.UserEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(userEntity: UserEntity): Long

    @Query("SELECT * FROM user_App")
    fun getUser(): UserEntity
}