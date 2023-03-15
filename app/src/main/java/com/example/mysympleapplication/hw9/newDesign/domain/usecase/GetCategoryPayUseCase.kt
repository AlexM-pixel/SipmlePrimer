package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.ModelsSpendsDbRepository
import javax.inject.Inject

class GetCategoryPayUseCase @Inject constructor(private val repo: ModelsSpendsDbRepository) {
       suspend fun getModelsSpends() = repo.getAllNameSpends()
        fun getModelsSpendsUnS() = repo.getAllNameSpendsUnS()

}