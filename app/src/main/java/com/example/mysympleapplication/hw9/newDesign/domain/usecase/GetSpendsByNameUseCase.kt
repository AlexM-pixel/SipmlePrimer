package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.SpendsDbRepository
import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class GetSpendsByNameUseCase @Inject constructor(private val repo: SpendsDbRepository) {
    suspend fun getListSpendsByName(name: String, dateMonth: String) =
        repo.getDetailSpendsByName(name, dateMonth)
}