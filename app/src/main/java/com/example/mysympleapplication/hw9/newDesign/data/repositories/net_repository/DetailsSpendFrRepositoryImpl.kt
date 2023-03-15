package com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository

import com.example.mysympleapplication.hw9.newDesign.data.mapper.DetailsSpendMapper
import com.example.mysympleapplication.hw9.newDesign.domain.model.DetailsSpend
import com.example.mysympleapplication.hw9.newDesign.domain.model.UserDocuments
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DetailsSpendFrRepositoryImpl @Inject constructor(private val fr: FirebaseFirestore, private val mapper: DetailsSpendMapper):DetailsSpendFrRepository {
    override suspend fun saveDetailsSpend(mail: String, detailsSpend: DetailsSpend) {
        fr
            .collection(mail)
            .document(UserDocuments.DETAILS_SPEND.name)
            .collection(UserDocuments.DETAILS_SPEND.name)
            .document(detailsSpend.id.toString())
            .set(mapper.mapToEntity(detailsSpend))
            .await()

    }

    override suspend fun getDetailsSpend(mail: String): List<DetailsSpend> {
        val snapshot = fr
            .collection(mail)
            .document(UserDocuments.DETAILS_SPEND.name)
            .collection(UserDocuments.DETAILS_SPEND.name)
            .get()
            .await()
        return snapshot.toObjects(DetailsSpend::class.java)
    }
}