package com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository

import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend

interface FireStoreRepository {
   suspend fun saveSpendFrStore(spend: Spend)
}