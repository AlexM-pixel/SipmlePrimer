package com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository

import android.util.Log
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.BalanceEntity
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.SpendEntity
import com.example.mysympleapplication.hw9.newDesign.data.mapper.BalanceMapper
import com.example.mysympleapplication.hw9.newDesign.data.mapper.SpendsMapper
import com.example.mysympleapplication.hw9.newDesign.domain.model.Balance
import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import com.example.mysympleapplication.hw9.newDesign.domain.model.SumSpendsOfMonth
import com.example.mysympleapplication.hw9.newDesign.domain.model.UserDocuments
import com.example.mysympleapplication.hw9.newDesign.utils.Result
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FriendsDataRepositoryImpl @Inject constructor(
    private val fr: FirebaseFirestore,
    private val mapper: BalanceMapper,
    private val spendMapper: SpendsMapper
) : FriendsDataRepository {
    override suspend fun getFriendsBalance(mail: String): Balance? {
        val snapshot = fr
            .collection(mail)
            .document(UserDocuments.BALANCE.name)
            .collection(UserDocuments.BALANCE.name)
            .document("0")
            .get()
            .await()
        val res =
            snapshot.toObject(BalanceEntity::class.java)
        Log.e("login", "getFriendBalanceFirestore: ${res?.toString()}")
        return res?.let { mapper.mapFromEntity(it) }
    }

    override suspend fun getFriendsExpensesByMonth(date: String): SumSpendsOfMonth {
        TODO("Not yet implemented")
//        val snapshot = fr
//            .collection(mail)
//            .document("spends")
//            .collection("spends")
//            .whereGreaterThanOrEqualTo("date",date)
//            .get()
//            .await()
//        val res=snapshot.
    }


    override suspend fun getTestFriendsExpensesByMonth(
        date: String,
        mail: String
    ): Result<Exception, List<Spend>> {
        val snapshot = fr
            .collection(mail)
            .document("spends")
            .collection("spends")
            .whereGreaterThanOrEqualTo("date", date)
            .get()
            .await()
        val res = snapshot.toObjects(SpendEntity::class.java)
        Log.e(
            "loginUser", "getSpendsFirestore: allItems.size = ${res.size} "
        )

        return Result.build { spendMapper.fromEntityList(res) }
    }

    override suspend fun getFriendProfile(mail: String): Result<Exception, Pair<String, String>> {
        return Result.build {
            // Запрашиваем документ
            val snapshot = fr
                .collection(mail)
                .document(UserDocuments.USERNAME.name)
                .get()
                .await() // await() замораживает корутину до получения результата

            // Парсим данные с защитой от null (старые аккаунты)
            val avatarName = snapshot.getString("avatarName") ?: "ic_baseline_person_24"
            val name = snapshot.getString("name") ?: mail.substringBefore("@")

            // Возвращаем Pair (Пару значений: Имя и Аватарка)
            Pair(name, avatarName)
        }
    }

}

private fun <T> Iterable<T>.sumOf(selector: (T) -> Float): Float {
    var sum = 0f
    for (element in this) {
        Log.e(
            "SumSpendsOfMonth", "element: == $element "
        )
        sum += selector(element)
    }
    return sum
}




