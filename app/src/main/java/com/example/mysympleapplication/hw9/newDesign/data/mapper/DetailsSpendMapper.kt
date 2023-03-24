package com.example.mysympleapplication.hw9.newDesign.data.mapper

import com.example.mysympleapplication.hw9.newDesign.data.entity_model.DetailsSpendEntity
import com.example.mysympleapplication.hw9.newDesign.domain.model.DetailsSpend
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DetailsSpendMapper @Inject constructor() : EntityMapper<DetailsSpendEntity, DetailsSpend> {
    override fun mapFromEntity(entity: DetailsSpendEntity): DetailsSpend {
        return DetailsSpend(
            id = entity.id,
            name = entity.name,
            value = entity.value,
            fKey = entity.fKey
        )
    }

    override fun mapToEntity(domainModel: DetailsSpend): DetailsSpendEntity {
        return DetailsSpendEntity(
            id = domainModel.id,
            name = domainModel.name,
            value = domainModel.value,
            fKey = domainModel.fKey
        )
    }

    fun fromEntityListFlow(listEntity: Flow<List<DetailsSpendEntity>>): Flow<List<DetailsSpend>> {
        return listEntity.map { it -> it.map { mapFromEntity(it) } }
    }
    fun fromEntityList(listEntity: List<DetailsSpendEntity>): List<DetailsSpend> {
        return listEntity.map { mapFromEntity(it)}
    }
}