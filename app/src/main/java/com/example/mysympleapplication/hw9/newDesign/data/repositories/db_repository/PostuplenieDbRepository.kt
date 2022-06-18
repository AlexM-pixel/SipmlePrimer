package com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository

import com.example.mysympleapplication.hw9.model.SumPostValue
import com.example.mysympleapplication.hw9.newDesign.domain.model.Postuplenie

interface PostuplenieDbRepository {
    suspend fun savePostuplenie(postuplenie: Postuplenie)
    suspend fun getAllPosuplenie():List<Postuplenie>
    suspend fun getMonthPostuplenie(date:String):SumPostValue?
}