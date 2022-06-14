package com.example.mysympleapplication.hw9.newDesign.data.mapper

import android.util.Log
import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.SpendEntity
import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.NameSpendsDbRepository
import com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository.FirestorageRepository
import javax.inject.Inject

class SpendsMapper @Inject constructor(
    private val repo: NameSpendsDbRepository,
    private val repoFr: FirestorageRepository
) :
    EntityMapper<SpendEntity, Spend> {
    override fun mapFromEntity(entity: SpendEntity): Spend {
        return Spend(
            id = entity.id,
            spendName = entity.spendName,
            value = entity.value,
            date = entity.date,
            url = null
        )
    }


    override fun mapToEntity(domainModel: Spend): SpendEntity {
        return SpendEntity(
            id = domainModel.id,
            spendName = domainModel.spendName,
            value = domainModel.value,
            date = domainModel.date
        )
    }

      fun fromEntityList(entityList: List<SpendEntity>): List<Spend> {
        return entityList.map { mapFromEntity(it) }
    }
}