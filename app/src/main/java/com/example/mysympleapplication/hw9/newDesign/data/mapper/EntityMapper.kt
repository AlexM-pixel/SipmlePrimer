package com.example.mysympleapplication.hw9.newDesign.data.mapper

interface EntityMapper<Entity,DomainModel> {
    fun mapFromEntity(entity: Entity):DomainModel
    fun mapToEntity(domainModel: DomainModel):Entity
}