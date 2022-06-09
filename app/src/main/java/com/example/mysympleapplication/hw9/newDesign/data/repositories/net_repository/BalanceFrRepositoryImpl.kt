package com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository

import android.util.Log
import com.example.mysympleapplication.hw9.newDesign.data.db.db_sours.UsersDbSours
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.BalanceEntity
import com.example.mysympleapplication.hw9.newDesign.data.mapper.BalanceMapper
import com.example.mysympleapplication.hw9.newDesign.data.net.FirestoreSource
import com.example.mysympleapplication.hw9.newDesign.domain.model.Balance
import com.example.mysympleapplication.hw9.newDesign.domain.model.UserDocuments
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class BalanceFrRepositoryImpl @Inject constructor(private val fr: FirebaseFirestore,private val mapper: BalanceMapper) :
    BalanceFrRepository {
    override suspend fun saveBalanceFrStore(mail: String,balance: Balance) {
        fr
            .collection(mail)
            .document(UserDocuments.BALANCE.name)
            .collection(UserDocuments.BALANCE.name)
            .document(balance.id.toString())
            .set(mapper.mapToEntity(balance))
            .await()
    }

    override suspend fun getBalanceFrStore(mail: String): Balance? {
        val snapshot = fr
            .collection(mail)
            .document(UserDocuments.BALANCE.name)
            .get()
            .await()
        val res = snapshot.toObject(BalanceEntity::class.java)
        Log.e("login", "getBalanceFirestore: ${res?.balance}")
        return res?.let { mapper.mapFromEntity(it) }
    }
}