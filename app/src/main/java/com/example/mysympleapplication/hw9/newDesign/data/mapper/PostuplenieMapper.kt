package com.example.mysympleapplication.hw9.newDesign.data.mapper

import com.example.mysympleapplication.hw9.newDesign.data.entity_model.NameSpendsEntity
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.PostuplenieEntity
import com.example.mysympleapplication.hw9.newDesign.domain.model.NameSpend
import com.example.mysympleapplication.hw9.newDesign.domain.model.Postuplenie
import javax.inject.Inject

class PostuplenieMapper @Inject constructor() : EntityMapper<PostuplenieEntity, Postuplenie> {
    override fun mapFromEntity(entity: PostuplenieEntity): Postuplenie {
        return Postuplenie(
            id = entity.id,
            value = entity.value,
            date = entity.date
        )
    }

    override fun mapToEntity(domainModel: Postuplenie): PostuplenieEntity {
        return PostuplenieEntity(
            id = domainModel.id,
            value = domainModel.value,
            date = domainModel.date
        )
    }
    fun fromEntityList(listEntity:List<PostuplenieEntity>):List<Postuplenie>{
        return listEntity.map { mapFromEntity(it)}
    }
}