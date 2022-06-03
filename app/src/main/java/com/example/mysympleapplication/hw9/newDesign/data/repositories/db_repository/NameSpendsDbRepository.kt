package com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository

import com.example.mysympleapplication.hw9.newDesign.data.entity_model.NameSpendsEntity
import com.example.mysympleapplication.hw9.newDesign.domain.model.NameSpend

interface NameSpendsDbRepository {
    suspend fun insertNameSpend(nameSpends: NameSpend)
    suspend fun getAllNameSpends(): List<NameSpend>
    suspend fun deleteNameSpend(id: Long)
    suspend fun insertAllNameSpends(list: List<NameSpend>)
}