package com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository

import com.google.firebase.auth.AuthResult
import com.example.mysympleapplication.hw9.newDesign.utils.Result

interface AuthRepository {

    suspend fun registrationUserByEmail(
        email: String,
        password: String
    ): Result<Exception, AuthResult>

    suspend fun signInUser(email: String, password: String): Result<Exception, AuthResult>

     fun signOutCurrentUser(): Result<Exception, Unit>
    suspend fun sendPasswordResetEmail(email: String): Result<Exception, Unit>
    suspend fun createNewUserFirestore(userName:String,email:String): Result<Exception, Void>
}