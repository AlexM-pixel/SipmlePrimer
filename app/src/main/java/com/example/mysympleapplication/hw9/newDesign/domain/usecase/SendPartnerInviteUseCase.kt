package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository.FriendsDataRepository
import javax.inject.Inject

class SendPartnerInviteUseCase @Inject constructor(private val repo: FriendsDataRepository) {
    suspend operator fun invoke(myEmail: String, friendEmail: String) =
        repo.sendInvite(myEmail, friendEmail)
}