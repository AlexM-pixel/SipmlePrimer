package com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository

import android.net.Uri

interface FirestorageRepository {
    suspend fun downloadImageUrl(name:String): Uri?
}