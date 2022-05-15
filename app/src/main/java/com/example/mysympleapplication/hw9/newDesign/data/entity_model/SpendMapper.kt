package com.example.mysympleapplication.hw9.newDesign.data.entity_model

import com.example.mysympleapplication.hw9.newDesign.data.mapper.EntityMapper
import com.example.mysympleapplication.hw9.newDesign.domain.model.User

class SpendMapper : EntityMapper<UserEntity, User> {
    override fun mapFromEntity(entity: UserEntity): User {
        return User(
            id = entity.id,
            name = entity.name,
            email = entity.email,
            friendsName = entity.friendsName,
            isHaseFriends = entity.isHaseFriends
        )
    }

    override fun mapToEntity(domainModel: User): UserEntity {
        TODO("Not yet implemented")
    }

    fun fromEntityList(initial: List<UserEntity>): List<User> {
        return initial.map { mapFromEntity(it) }
    }
}