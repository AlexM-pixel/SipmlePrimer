package com.example.mysympleapplication.hw9.newDesign.di.module

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mysympleapplication.hw9.newDesign.data.db.AppDataBase
import com.example.mysympleapplication.hw9.newDesign.data.db.dao.*
import com.example.mysympleapplication.hw9.newDesign.data.db.rePopulateDb
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
class StorageModule {
    @DelicateCoroutinesApi
    @Singleton
    @Provides
    fun provideDb(context: Context): AppDataBase {
        var appDb: AppDataBase? = null
        appDb = Room
                .databaseBuilder(context, AppDataBase::class.java, AppDataBase.DB_NAME)
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        GlobalScope.launch(Dispatchers.IO) { rePopulateDb(appDb) }
                    }
                })
                .build()
        return appDb
    }

    @Singleton
    @Provides
    fun provideUserDao(db: AppDataBase): UserDao {
        return db.userDao()
    }

    @Singleton
    @Provides
    fun providePostuplenieDao(db: AppDataBase): PostuplenieDao {
        return db.postuplenieDao()
    }

    @Singleton
    @Provides
    fun provideBalanceDao(db: AppDataBase): BalanceDao {
        return db.balanceDao()
    }

    @Singleton
    @Provides
    fun provideFriendsDao(db: AppDataBase): FriendsDao {
        return db.friendsDao()
    }

    @Singleton
    @Provides
    fun provideSpendDao(db: AppDataBase): SpendDao {
        return db.spendDao()
    }
}