package com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository

import com.example.mysympleapplication.hw9.model.SumPostValue
import com.example.mysympleapplication.hw9.newDesign.data.db.AppDataBase
import com.example.mysympleapplication.hw9.newDesign.data.mapper.PostuplenieMapper
import com.example.mysympleapplication.hw9.newDesign.domain.model.Postuplenie
import javax.inject.Inject

class PostuplenieDbRepositoryImpl @Inject constructor(private val db: AppDataBase,
                                                      private val mapper: PostuplenieMapper
): PostuplenieDbRepository {
    override suspend fun savePostuplenie(postuplenie: Postuplenie) {
      db.postuplenieDao().insert(mapper.mapToEntity(postuplenie))
    }

    override suspend fun getAllPosuplenie(): List<Postuplenie> {
      return  mapper.fromEntityList(db.postuplenieDao().getAllPostyplenie())
    }

    override suspend fun getMonthPostuplenie(date: String):SumPostValue? {
        return db.postuplenieDao().getMonthPostuplenie()
    }
}