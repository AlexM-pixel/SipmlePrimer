package com.example.mysympleapplication.hw9.newDesign.data.db

import android.content.Context
import android.util.Log

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mysympleapplication.hw9.newDesign.data.db.dao.*
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Database(
    entities = [UserEntity::class, SpendEntity::class, PostuplenieEntity::class, BalanceEntity::class,
        FriendsSpendsEntity::class, FriendsBalanceEntity::class, FriendsPostuplenieEntity::class,NameSpendsEntity::class],
    version = 1
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun postuplenieDao(): PostuplenieDao
    abstract fun spendDao(): SpendDao
    abstract fun balanceDao(): BalanceDao
    abstract fun friendsDao(): FriendsDao
    abstract fun namesSpendsDao():NameSpendsDao

    companion object {
        const val DB_NAME = "my_expenses.db"
        private var INSTANCE: AppDataBase? = null


        fun getDatabase(context: Context): AppDataBase {
            if (INSTANCE == null) {
                synchronized(AppDataBase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.applicationContext, AppDataBase::class.java, DB_NAME)
                            .addCallback(object : Callback() {
                                override fun onCreate(db: SupportSQLiteDatabase) {
                                    super.onCreate(db)
                                    Log.e("AppDataBase", "onCreate Db, ")
                                    GlobalScope.launch(Dispatchers.IO) { rePopulateDb(INSTANCE) }
                                }
                            }).build()
                    }
                }
            }

            return INSTANCE!!
        }
    }
}