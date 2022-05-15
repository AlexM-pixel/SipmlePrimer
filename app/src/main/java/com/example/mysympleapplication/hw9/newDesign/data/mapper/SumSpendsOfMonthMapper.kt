package com.example.mysympleapplication.hw9.newDesign.data.mapper

import com.example.mysympleapplication.hw9.SumSpendsOfMonth
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.SumSpendsOfMonthEntity
import javax.inject.Inject

class SumSpendsOfMonthMapper @Inject constructor() : EntityMapper<SumSpendsOfMonthEntity, SumSpendsOfMonth> {
    override fun mapFromEntity(entity: SumSpendsOfMonthEntity): SumSpendsOfMonth {
        return SumSpendsOfMonth(
            entity.dateM, entity.valueSpends
        )
    }

    override fun mapToEntity(domainModel: SumSpendsOfMonth): SumSpendsOfMonthEntity {
      return SumSpendsOfMonthEntity(
          dateM = domainModel.dateM,
          valueSpends = domainModel.value_spends
      )
    }

    fun fromEntityList(initial: List<SumSpendsOfMonthEntity>): List<SumSpendsOfMonth> {
        return initial.map { mapFromEntity(it) }
    }
}