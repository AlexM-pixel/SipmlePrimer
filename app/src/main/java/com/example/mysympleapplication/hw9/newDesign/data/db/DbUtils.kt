package com.example.mysympleapplication.hw9.newDesign.data.db

import com.example.mysympleapplication.hw9.NameSpends
import com.example.mysympleapplication.hw9.newDesign.data.db.dao.NameSpendsDao
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.NameSpendsEntity
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
                    nameSpend=item.nameSpand,
                    ruName = item.russianName,
                    imageUri = getImageUrl()

                )
                )
            }
            nameSpendsDao.insertAllNamesSpends(listNames)
        }
    }
}

fun getImageUrl(): String {

return ""
}
