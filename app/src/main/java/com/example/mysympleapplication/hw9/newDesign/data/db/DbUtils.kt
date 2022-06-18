package com.example.mysympleapplication.hw9.newDesign.data.db

import android.util.Log
import com.example.mysympleapplication.hw9.NameSpends
import com.example.mysympleapplication.hw9.newDesign.data.db.dao.NameSpendsDao
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.NameSpendsEntity
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.DownloadImageUrlUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun rePopulateDb(database: AppDataBase?) {
    database?.let { db ->
        withContext(Dispatchers.IO) {
            val nameSpendsDao: NameSpendsDao = db.namesSpendsDao()
            val listNames: ArrayList<NameSpendsEntity> = arrayListOf()
            for (item in NameSpends.values()) {
                listNames.add(
                    NameSpendsEntity(
                        nameSpend = item.nameSpand,
                        ruName = item.russianName,
                        imageName = item.imageName
                    )
                )
            }
            Log.e("AppDataBase", "insertAllNamesSpends, size: ${listNames.size}")
            nameSpendsDao.insertAllNamesSpends(listNames)
        }
    }
}
