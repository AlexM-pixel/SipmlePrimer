package com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository

import com.example.mysympleapplication.hw9.newDesign.data.db.AppDataBase
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.NameSpendsEntity
import com.example.mysympleapplication.hw9.newDesign.data.mapper.NameSpendsMapper
import com.example.mysympleapplication.hw9.newDesign.domain.model.NameSpend
import javax.inject.Inject

class NameSpendsDbRepositoryImpl @Inject constructor(
    private val db: AppDataBase,
    private val mapper: NameSpendsMapper
) : NameSpendsDbRepository {

    override suspend fun insertNameSpend(nameSpends: NameSpend) {
        db.namesSpendsDao().insertNameSpend(mapper.mapToEntity(nameSpends))
    }

    override suspend fun getAllNameSpends() =
        mapper.fromEntityList(db.namesSpendsDao().getAllNamesSpends())


    override suspend fun deleteNameSpend(id: Long) {
        db.namesSpendsDao().deleteNameSpend(id)
    }

    override suspend fun insertAllNameSpends(list: List<NameSpend>) {
       db.namesSpendsDao().insertAllNamesSpends(mapper.toEntityList(list))
    }
}