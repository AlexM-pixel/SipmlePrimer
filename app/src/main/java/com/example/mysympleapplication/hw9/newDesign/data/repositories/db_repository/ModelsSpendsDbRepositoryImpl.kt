package com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository

import com.example.mysympleapplication.hw9.newDesign.data.db.AppDataBase
import com.example.mysympleapplication.hw9.newDesign.data.mapper.NameSpendsMapper
import com.example.mysympleapplication.hw9.newDesign.domain.model.NameSpend
import javax.inject.Inject

class ModelsSpendsDbRepositoryImpl @Inject constructor(
    private val db: AppDataBase,
    private val mapper: NameSpendsMapper
) : ModelsSpendsDbRepository {
    override fun insertNameSpend(nameSpends: NameSpend) {
        db.namesSpendsDao().insertNameSpend(mapper.mapToEntity(nameSpends))
    }

    override suspend fun getAllNameSpends(): List<NameSpend> {
        return mapper.fromEntityList(db.namesSpendsDao().getAllNamesSpends())
    }

    override fun getAllNameSpendsUnS(): List<NameSpend> {
        return mapper.fromEntityList(db.namesSpendsDao().getAllNamesNoSuspendSpends())
    }

    override fun deleteNameSpend(id: Long) {
       db.namesSpendsDao().deleteNameSpend(id)
    }

}