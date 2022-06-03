package com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestorageRepositoryImpl @Inject constructor(val refStorage: StorageReference) :
    FirestorageRepository {

    override suspend fun downloadImageUrl(name: String): Uri? {
        return refStorage
            .child(name)
            .downloadUrl
            .await()
    }
}