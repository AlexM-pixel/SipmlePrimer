package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import android.util.Log
import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.NameSpendsDbRepository
import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.PostuplenieDbRepository
import com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository.PostuplenieFrRepository
import com.example.mysympleapplication.hw9.newDesign.domain.model.NameSpend
import com.example.mysympleapplication.hw9.newDesign.domain.model.Postuplenie
import javax.inject.Inject

class SavePostuplenieUseCase @Inject constructor(
    private val repoDb: PostuplenieDbRepository,
    private val repoFr: PostuplenieFrRepository
) {
    suspend fun savePost(post: Postuplenie, mail: String) {
        try {
            repoDb.savePostuplenie(postuplenie = post)
            repoFr.savePostuplenie(mail = mail, postuplenie = post)
        } catch (e: Exception) {
            Log.e("SavePostUseCase", "ERROR savePostuplenie: ${e.message}")
        }
    }

}