package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository.FriendsDataRepository
import com.example.mysympleapplication.hw9.newDesign.domain.model.PartnerState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePartnerStateUseCase @Inject constructor(private val repo: FriendsDataRepository) {
    operator fun invoke(myEmail: String): Flow<PartnerState> {
        return repo.observePartnerState(myEmail)
    }
}