package com.example.mysympleapplication.hw9.newDesign.data.entity_model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.util.TableInfo

@Entity(
    tableName = "detailsSpend", foreignKeys = [ForeignKey(
        entity = SpendEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("fKey"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class DetailsSpendEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val value: String,
    val fKey: Long
)