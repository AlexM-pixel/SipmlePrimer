package com.example.mysympleapplication.hw9.newDesign.data.entity_model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "name_spends")
data class NameSpendsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val nameSpend: String = "",
    val ruName: String = "",
    val imageUri: String = ""
) {
    fun getImage(name: String): String {

        return imageUri
    }
}