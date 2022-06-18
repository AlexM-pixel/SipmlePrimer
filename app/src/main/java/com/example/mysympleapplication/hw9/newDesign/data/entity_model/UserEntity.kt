package com.example.mysympleapplication.hw9.newDesign.data.entity_model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_App")
data class UserEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val email: String,
    val friendsName: String,
    val isHaseFriends: Boolean,
    val balance: String
)
