package com.example.mysympleapplication.hw9.newDesign.data.mapper

import com.example.mysympleapplication.hw9.newDesign.data.entity_model.NameSpendsEntity
import com.example.mysympleapplication.hw9.newDesign.domain.model.NameSpend
import javax.inject.Inject

class NameSpendsMapper @Inject constructor() : EntityMapper<NameSpendsEntity, NameSpend> {
    override fun mapFromEntity(entity: NameSpendsEntity): NameSpend {
        return NameSpend(
            id = entity.id ?: 0,
            nameSpend = entity.nameSpend,
            ruName = entity.ruName,
            imageName = entity.imageName
        )
    }

    override fun mapToEntity(domainModel: NameSpend): NameSpendsEntity {
        return NameSpendsEntity(
            id = domainModel.id,
            nameSpend = domainModel.nameSpend,
            ruName = domainModel.ruName,
            imageName = domainModel.imageName
        )
    }

    fun fromEntityList(listEntity:List<NameSpendsEntity>):List<NameSpend>{
       return listEntity.map { mapFromEntity(it)}
    }

    fun toEntityList(list:List<NameSpend>):List<NameSpendsEntity>{
        return list.map { mapToEntity(it)}
    }
}