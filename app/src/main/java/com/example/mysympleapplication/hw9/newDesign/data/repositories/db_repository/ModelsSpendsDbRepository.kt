package com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository

import com.example.mysympleapplication.hw9.newDesign.domain.model.NameSpend

interface ModelsSpendsDbRepository {
     fun insertNameSpend(nameSpends: NameSpend)
     fun getAllNameSpends(): List<NameSpend>
     fun deleteNameSpend(id: Long)
}