package com.example.mysympleapplication.hw9.newDesign.data.mapper

import com.example.mysympleapplication.hw9.newDesign.data.entity_model.BankCardEntity
import com.example.mysympleapplication.hw9.newDesign.domain.model.BankCard
import javax.inject.Inject

class BankCardMapper @Inject constructor(): EntityMapper<BankCardEntity, BankCard>{
    override fun mapFromEntity(entity: BankCardEntity): BankCard {
        return BankCard(
            id = entity.id,
            cardName = entity.cardName,
            lastFourDigits = entity.lastFourDigits,
            balance = entity.balance,
            currency = entity.currency
        )
    }

    override fun mapToEntity(domainModel: BankCard): BankCardEntity {
        return BankCardEntity(
            id = domainModel.id,
            cardName = domainModel.cardName,
            lastFourDigits = domainModel.lastFourDigits,
            balance = domainModel.balance,
            currency = domainModel.currency
        )
    }

}