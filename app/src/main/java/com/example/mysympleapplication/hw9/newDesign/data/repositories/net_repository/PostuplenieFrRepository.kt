package com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository

import com.example.mysympleapplication.hw9.newDesign.domain.model.Postuplenie

interface PostuplenieFrRepository {
    suspend fun savePostuplenie(mail:String,postuplenie: Postuplenie)
    suspend fun getPostuplenie(mail:String):List<Postuplenie>
}