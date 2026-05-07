package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository.FriendsDataRepository
import com.example.mysympleapplication.hw9.newDesign.utils.Result
import javax.inject.Inject

class GetFriendProfileUseCase @Inject constructor(
    private val repository: FriendsDataRepository
) {
    suspend operator fun invoke(mail: String): Result<Exception, Pair<String, String>> {
        return repository.getFriendProfile(mail)
    }
}