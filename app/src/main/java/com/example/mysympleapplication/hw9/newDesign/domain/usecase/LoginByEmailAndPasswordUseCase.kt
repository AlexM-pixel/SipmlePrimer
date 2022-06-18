package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository.AuthRepository
import javax.inject.Inject

class LoginByEmailAndPasswordUseCase @Inject constructor(private val authRepo: AuthRepository) {
    suspend operator fun invoke(email: String, password: String) =
        authRepo.signInUser(email, password)
}