package com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository

import com.example.mysympleapplication.hw9.newDesign.data.db.AppDataBase
import com.example.mysympleapplication.hw9.newDesign.data.mapper.DetailsSpendMapper
import com.example.mysympleapplication.hw9.newDesign.domain.model.DetailsSpend
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DetailsSpendDbRepositoryImpl @Inject constructor(
    private val db: AppDataBase,
    private val mapper: DetailsSpendMapper
) : DetailsSpendDbRepository {
    override suspend fun getDetailsListFlow(id: Long): Flow<List<DetailsSpend>> {
        return mapper.fromEntityListFlow(db.detailsSpendDao().getDetailsBySpendFlow(idSpend = id))
    }

    override suspend fun getAllDetails(): List<DetailsSpend> {
        return mapper.fromEntityList(db.detailsSpendDao().getAllDetails())
    }

    override suspend fun insertDetail(detailsSpend: DetailsSpend) {
        db.detailsSpendDao().insertDetailBySpend(mapper.mapToEntity(detailsSpend))
    }

    override suspend fun delDetails(id: Long) {
        db.detailsSpendDao().delDetail(id = id)
    }

}