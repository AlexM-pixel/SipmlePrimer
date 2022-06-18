package com.example.mysympleapplication.hw9.newDesign.data.net

import android.util.Log
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.FriendsEntity
import com.example.mysympleapplication.hw9.newDesign.domain.model.UserDocuments
import com.example.mysympleapplication.hw9.newDesign.utils.Result
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthUserSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun createUserByEmail(email: String, password: String):
            Result<Exception, AuthResult> =
        try {
            val data = auth.createUserWithEmailAndPassword(email, password)
                .await()
            Result.build { data }
        } catch (e: Exception) {
            Result.build { throw e }
        }

    suspend fun signInUser(email: String, password: String):
            Result<Exception, AuthResult> =
        try {
            val data = auth.signInWithEmailAndPassword(email, password)
                .await()
            Result.build { data }
        } catch (e: Exception) {
            Result.build { throw e }
        }

    fun signOutCurrentUser(): Result<Exception, Unit> {
        return Result.build { auth.signOut() }
    }

    suspend fun sendPasswordResetEmail(email: String):
            Result<Exception, Unit> = Result.build { auth.sendPasswordResetEmail(email).await() }

    suspend fun creatingNewUserFirestore(name: String, email: String):
            Result<Exception, Void> =
        try {
           firestore.collection(email)
                .document(UserDocuments.USERNAME.name)
                .set(hashMapOf("name" to name))
                .await()
            Log.e("createUser", "creatingNewUserFirestore nameThread: ${Thread.currentThread().name}")
            createCollectionFriends(email)
        } catch (e: Exception) {
            Result.build { throw e }
        }

    private suspend fun createCollectionFriends(email: String): Result<Exception, Void> {
        val friend = FriendsEntity(false, "first_init")
        return try {
            val res = firestore.collection(email)
                .document(UserDocuments.FRIENDS.name)
                .collection(UserDocuments.FRIENDS.name)
                .document("first_init")
                .set(friend)
                .await()
            Log.e("createUser", "createCollectionFriends nameThread: ${Thread.currentThread().name}")
            Result.build { res }
        } catch (e: Exception) {
            return Result.build { throw e }
        }
    }

}