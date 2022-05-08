package com.example.mysympleapplication.hw9.newDesign.domain.mappers

interface EntityMapper<Entity,DomainModel> {
    fun mapFromEntity(entity: Entity):DomainModel
    fun mapToEntity(domainModel: DomainModel):Entity
}