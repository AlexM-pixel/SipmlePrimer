package com.example.mysympleapplication.hw9.newDesign.data.mapper

import com.example.mysympleapplication.hw9.newDesign.data.entity_model.BalanceEntity
import com.example.mysympleapplication.hw9.newDesign.domain.model.Balance
import javax.inject.Inject

class BalanceMapper @Inject constructor() : EntityMapper<BalanceEntity, Balance> {
    override fun mapFromEntity(entity: BalanceEntity): Balance {
        return Balance(
            id = entity.id,
            balance = entity.balance
        )
    }

    override fun mapToEntity(domainModel: Balance): BalanceEntity {
        return BalanceEntity(
            id = domainModel.id,
            balance = domainModel.balance
        )
    }
}