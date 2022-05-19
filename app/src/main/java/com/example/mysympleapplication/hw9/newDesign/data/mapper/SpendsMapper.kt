package com.example.mysympleapplication.hw9.newDesign.data.mapper

import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.SpendEntity

class SpendsMapper : EntityMapper<SpendEntity, Spend> {
    override fun mapFromEntity(entity: SpendEntity): Spend {
        return Spend(
            id = entity.id,
            spendName = entity.spendName,
            value = entity.value,
            date = entity.date
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
}