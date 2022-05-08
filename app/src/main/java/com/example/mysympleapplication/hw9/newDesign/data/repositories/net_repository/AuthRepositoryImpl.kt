package com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository

import com.google.firebase.auth.AuthResult
import javax.inject.Inject
import com.example.mysympleapplication.hw9.newDesign.utils.Result
import com.example.mysympleapplication.hw9.newDesign.data.net.FirebaseAuthUserSource


class AuthRepositoryImpl @Inject constructor(private val authSource: FirebaseAuthUserSource) :
    AuthRepository {

    override suspend fun registrationUserByEmail(email: String, password: String):
            Result<Exception, AuthResult> =
        authSource.createUserByEmail(email = email, password = password)

    override suspend fun signInUser(
        email: String,
        password: String
    ): Result<Exception, AuthResult> {
        return authSource.signInUser(email = email, password = password)
    }

    override fun signOutCurrentUser(): Result<Exception, Unit> {
        return authSource.signOutCurrentUser()
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Exception, Unit> {
        return authSource.sendPasswordResetEmail(email = email)
    }

    override suspend fun createNewUserFirestore(
        userName: String,
        email: String
    ): Result<Exception, Void> {
        return authSource.creatingNewUserFirestore(userName, email)
    }




}