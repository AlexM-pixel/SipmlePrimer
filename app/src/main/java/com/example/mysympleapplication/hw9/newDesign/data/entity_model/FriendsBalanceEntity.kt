package com.example.mysympleapplication.hw9.newDesign.data.entity_model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friend_balance")
data class FriendsBalanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val balance: String
)
