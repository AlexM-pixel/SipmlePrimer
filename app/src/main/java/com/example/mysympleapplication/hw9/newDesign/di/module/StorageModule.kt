package com.example.mysympleapplication.hw9.newDesign.di.module

import android.content.Context
import com.example.mysympleapplication.hw9.newDesign.data.db.AppDataBase
import com.example.mysympleapplication.hw9.newDesign.data.db.dao.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class StorageModule {

    @Singleton
    @Provides
    fun provideDb(context: Context): AppDataBase {
        return AppDataBase.getDatabase(context)
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

    @Singleton
    @Provides
    fun provideNamesSpendsDao(db: AppDataBase): NameSpendsDao {
        return db.namesSpendsDao()
    }
}