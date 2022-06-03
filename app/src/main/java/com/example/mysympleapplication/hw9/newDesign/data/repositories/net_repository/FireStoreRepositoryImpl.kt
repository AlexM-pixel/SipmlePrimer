package com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository

import com.example.mysympleapplication.hw9.newDesign.data.mapper.SpendsMapper
import com.example.mysympleapplication.hw9.newDesign.data.net.FirestoreSource
import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import javax.inject.Inject

class FireStoreRepositoryImpl @Inject constructor(
    private val fireStoreSource: FirestoreSource,
    private val mapper: SpendsMapper
) : FireStoreRepository {
    override suspend fun saveSpendFrStore(spend: Spend,mail:String) {
        fireStoreSource.insertSpend(mapper.mapToEntity(spend), mail = mail)
    }
}