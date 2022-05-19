package com.example.mysympleapplication.hw9.newDesign.data.net

import android.util.Log
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.BalanceEntity
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.SpendEntity
import com.example.mysympleapplication.hw9.newDesign.domain.model.UserDocuments
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreSource @Inject constructor(private val fr: FirebaseFirestore) {
    suspend fun getSpendsFirestore(mail: String): List<SpendEntity> {
        val snapshot = fr
            .collection(mail)
            .document("spends")
            .collection("spends")
            .get()
            .await()
        val spends =
            snapshot.toObjects(SpendEntity::class.java)
        Log.e(
            "loginUser", "getSpendsFirestore: allItems.size = ${spends.size} "
        )
        return spends
    }

    suspend fun getBalanceFirestore(mail: String): BalanceEntity? {
        val snapshot = fr
            .collection(mail)
            .document(UserDocuments.BALANCE.name)
            .get()
            .await()
        val res = snapshot.toObject(BalanceEntity::class.java)
        Log.e("login", "getBalanceFirestore: ${res?.balance}")
        return res
    }

    suspend fun insertSpend(spendEntity: SpendEntity){

    }
}