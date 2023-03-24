package com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository

import com.example.mysympleapplication.hw9.newDesign.domain.model.DetailsSpend
import kotlinx.coroutines.flow.Flow

interface DetailsSpendDbRepository {
    suspend fun getDetailsListFlow(id:Long): Flow<List<DetailsSpend>>
    suspend fun getAllDetails(): List<DetailsSpend>
    suspend fun insertDetail(detailsSpend: DetailsSpend)
    suspend fun delDetails(id: Long)
}