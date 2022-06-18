package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import android.net.Uri
import com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository.FirestorageRepository
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DownloadImageUrlUseCase @Inject constructor(private val repoFrStorage: FirestorageRepository) {
    operator fun invoke(name: String): Flow<Resource<Uri>> = flow {
        emit(Resource.Loading())
        try {
            val res = repoFrStorage.downloadImageUrl(name = name)
            if (res != null) {
                emit(Resource.Success(data = res))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }
}