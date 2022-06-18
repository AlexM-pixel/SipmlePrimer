package com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository

import android.util.Log
import com.example.mysympleapplication.hw9.newDesign.data.mapper.PostuplenieMapper
import com.example.mysympleapplication.hw9.newDesign.domain.model.Postuplenie
import com.example.mysympleapplication.hw9.newDesign.domain.model.UserDocuments
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PostuplenieFrRepoImpl @Inject constructor(private val fr: FirebaseFirestore,private val mapper: PostuplenieMapper)  :PostuplenieFrRepository {
    override suspend fun savePostuplenie(mail: String,postuplenie: Postuplenie) {
        fr
            .collection(mail)
            .document(UserDocuments.POSTUPLENIE.name)
            .collection(UserDocuments.POSTUPLENIE.name)
            .document(postuplenie.id.toString())
            .set(mapper.mapToEntity(postuplenie))
            .await()
    }

    override suspend fun getPostuplenie(mail: String): List<Postuplenie> {
        val snapshot = fr
            .collection(mail)
            .document(UserDocuments.POSTUPLENIE.name)
            .collection(UserDocuments.POSTUPLENIE.name)
            .get()
            .await()
        val postuplenie =
            snapshot.toObjects(Postuplenie::class.java)
        Log.e(
            "loginUser", "getSpendsFirestore: allItems.size = ${postuplenie.size} "
        )
        return postuplenie
    }
}