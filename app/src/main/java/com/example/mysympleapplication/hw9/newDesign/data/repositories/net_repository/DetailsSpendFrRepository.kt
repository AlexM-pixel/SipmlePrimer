package com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository

import com.example.mysympleapplication.hw9.newDesign.domain.model.DetailsSpend
import com.example.mysympleapplication.hw9.newDesign.domain.model.Postuplenie

interface DetailsSpendFrRepository {
    suspend fun saveDetailsSpend(mail:String,detailsSpend: DetailsSpend)
    suspend fun getDetailsSpend(mail:String):List<DetailsSpend>
    suspend fun delDetailsSpend(mail: String,id:String)
}