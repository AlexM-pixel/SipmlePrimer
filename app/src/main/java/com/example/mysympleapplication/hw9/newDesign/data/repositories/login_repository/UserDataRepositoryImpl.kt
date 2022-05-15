package com.example.mysympleapplication.hw9.newDesign.data.repositories.login_repository

import android.util.Log
import com.example.mysympleapplication.hw9.newDesign.data.db.db_sours.UsersDbSours
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.BalanceEntity
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.SpendEntity
import com.example.mysympleapplication.hw9.newDesign.data.net.FirestoreSource
import javax.inject.Inject

class UserDataRepositoryImpl @Inject constructor(
    private val dbUserSource: UsersDbSours,
    private val fireStoreUserSours: FirestoreSource
) :
    UserDataRepository {
    override suspend fun getExpensesWhileLogin(email: String) { // брать с двух сурсов и взависимсти что пришло = действовать
        val dbData: List<SpendEntity> = dbUserSource.getUsersDataWhileLogin()
        Log.e("loginUser", "dataBase: allItems.size = ${dbData.size} ")
        val firestoreData = fireStoreUserSours.getSpendsFirestore(mail = email)
        if (dbData.isEmpty() && firestoreData.isNotEmpty()) {
            dbUserSource.insertAllSpendsWhileLogin(firestoreData)
            Log.e(
                "loginUser",
                " repository in if(): dbSpends isEmpty= ${dbData.isEmpty()} , fireStore isEmpty= ${firestoreData.isEmpty()} "
            )
        }
        Log.e(
            "loginUser",
            " repository: dbSpends isEmpty= ${dbData.isEmpty()} , fireStore isEmpty= ${firestoreData.isEmpty()} "
        )
    }


    override suspend fun getBalanceWhileLogin(email: String) {
        val dbBalance: BalanceEntity? = dbUserSource.getBalance()
        val firestoreBalance = fireStoreUserSours.getBalanceFirestore(email)
        if (firestoreBalance != null && dbBalance==null  && firestoreBalance.balance.isNotEmpty()) {
                dbUserSource.insertBalance(firestoreBalance)
            Log.e(
                "loginUser",
                " getBalanceWhileLogin in if(): balance.isEmpty()= ${dbBalance?.balance?.isEmpty()} , fireStoreBalance isEmpty= ${firestoreBalance.balance.isEmpty()} "
            )
        }
        Log.e(
            "loginUser",
            " getBalanceWhileLogin: dbBalance.isEmpty=${dbBalance?.balance?.isEmpty()} , fireStore isEmpty= ${firestoreBalance?.balance?.isEmpty()} "
        )
    }


}