package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.ModelsSpendsDbRepository
import javax.inject.Inject

class GetModelsSpendsUseCase @Inject constructor(private val repo: ModelsSpendsDbRepository) {
    fun getModelsSpends() = repo.getAllNameSpends()
}