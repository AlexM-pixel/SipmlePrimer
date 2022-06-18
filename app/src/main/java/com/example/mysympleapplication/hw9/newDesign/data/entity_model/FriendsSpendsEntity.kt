package com.example.mysympleapplication.hw9.newDesign.data.entity_model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friend_spends")
data class FriendsSpendsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val spendName: String,
    val value: String,
    val date: String
)
