package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository.AuthRepository
import javax.inject.Inject

class LogOutUseCase @Inject constructor(private val authRepo: AuthRepository) {
     operator fun invoke() =
        authRepo.signOutCurrentUser()
}