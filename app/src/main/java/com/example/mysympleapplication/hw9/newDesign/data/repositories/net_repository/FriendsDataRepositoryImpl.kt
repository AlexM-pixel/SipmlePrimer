package com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository

import android.util.Log
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.BalanceEntity
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.SpendEntity
import com.example.mysympleapplication.hw9.newDesign.data.mapper.BalanceMapper
import com.example.mysympleapplication.hw9.newDesign.data.mapper.SpendsMapper
import com.example.mysympleapplication.hw9.newDesign.domain.model.Balance
import com.example.mysympleapplication.hw9.newDesign.domain.model.PartnerState
import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import com.example.mysympleapplication.hw9.newDesign.domain.model.SumSpendsOfMonth
import com.example.mysympleapplication.hw9.newDesign.domain.model.UserDocuments
import com.example.mysympleapplication.hw9.newDesign.utils.Result
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
            val avatarName = snapshot.getString("avatarName") ?: "place_holder_av"
            val name = snapshot.getString("name") ?: mail.substringBefore("@")

            // Возвращаем Pair (Пару значений: Имя и Аватарка)
            Pair(name, avatarName)
        }
    }

    /**
     * 1. Отправка приглашения
     */
    override suspend fun sendInvite(myEmail: String, friendEmail: String): Result<Exception, Unit> {
        return try {
            // Пишем себе: "Я отправил заявку"
            val myData = hashMapOf("partnerEmail" to friendEmail, "status" to "SENT")
            fr.collection(myEmail).document("PARTNER").set(myData).await()

            // Пишем другу: "Тебе пришла заявка"
            val friendData = hashMapOf("partnerEmail" to myEmail, "status" to "RECEIVED")
            fr.collection(friendEmail).document("PARTNER").set(friendData).await()

            Result.build { Unit }
        } catch (e: Exception) {
            Result.build { throw e }
        }
    }

    /**
     * 2. Ответ на приглашение (Принять / Отклонить) или Удаление
     */
    override suspend fun respondToInvite(myEmail: String, friendEmail: String, isAccepted: Boolean): Result<Exception, Unit> {
        return try {
            if (isAccepted) {
                // Приняли: ставим статус ACCEPTED у обоих
                fr.collection(myEmail).document("PARTNER").update("status", "ACCEPTED").await()
                fr.collection(friendEmail).document("PARTNER").update("status", "ACCEPTED").await()
            } else {
                // Отклонили / Удалили: просто стираем документ PARTNER у обоих
                fr.collection(myEmail).document("PARTNER").delete().await()
                fr.collection(friendEmail).document("PARTNER").delete().await()
            }
            Result.build { Unit }
        } catch (e: Exception) {
            Result.build { throw e }
        }
    }

    /**
     * 3. REAL-TIME слушатель состояний (Слушает изменения в БД мгновенно)
     */
    override fun observePartnerState(myEmail: String): Flow<PartnerState> = callbackFlow {
        if (myEmail.isEmpty()) {
            trySend(PartnerState.None)
            return@callbackFlow
        }

        // Подписываемся на документ PARTNER
        val listener = fr.collection(myEmail).document("PARTNER")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(PartnerState.None)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val partnerEmail = snapshot.getString("partnerEmail") ?: ""
                    val status = snapshot.getString("status") ?: ""

                    when (status) {
                        "ACCEPTED" -> trySend(PartnerState.Accepted("Загрузка...", partnerEmail, "ic_baseline_person_24"))
                        "SENT" -> trySend(PartnerState.Sent(partnerEmail))
                        "RECEIVED" -> trySend(PartnerState.Received(partnerEmail))
                        else -> trySend(PartnerState.None)
                    }
                } else {
                    // Документа нет -> связи нет
                    trySend(PartnerState.None)
                }
            }

        // Обязательно отписываемся от слушателя, когда UI умирает
        awaitClose { listener.remove() }
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




