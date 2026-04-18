package com.example.mysympleapplication.hw9.newDesign.data.net

import android.util.Log
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.SpendEntity
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class FirestoreSourceExample @Inject constructor(private val firestore: FirebaseFirestore) {
    fun getAllSpendsFirestore(mail: String): List<SpendEntity> {
        val allItems = mutableListOf<SpendEntity>()
        var id: Long
        var name: String
        var value: String
        var date: String
        var spend: SpendEntity
        firestore
            .collection(mail)
            .document("spends")
            .collection("spends")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        id = document.getLong("id") ?: 0
                        name = document["spendName"].toString()
                        value = document["value"].toString()
                        date = document["date"].toString()
                        spend = SpendEntity(id = id, spendName = name, value = value, date = date)
                        allItems.add(spend)
                    }
                } else {
                    Log.e("loginUser", "Error getting documents." + task.exception)
                }
                if (task.isComplete) {
                    Log.e(
                        "loginUser",
                        "return@addOnCompleteListener allItems.size = ${allItems.size} "
                    )
                 //   return@addOnCompleteListener allItems
                }
            }
        Log.e("loginUser", "---return allItems.size = ${allItems.size} ")
        return allItems
    }

}