package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.DetailsSpendDbRepository
import javax.inject.Inject

class GetDetailsFlowUseCase @Inject constructor(private val repo: DetailsSpendDbRepository) {

    suspend fun getList(id: Long) = repo.getDetailsListFlow(id)

}