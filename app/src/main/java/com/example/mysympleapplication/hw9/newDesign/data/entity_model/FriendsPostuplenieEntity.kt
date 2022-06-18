package com.example.mysympleapplication.hw9.newDesign.data.entity_model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friend_postuplenie")
data class FriendsPostuplenieEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Long,
    val value:String,
    val date:String
)
