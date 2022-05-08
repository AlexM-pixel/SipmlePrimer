package com.example.mysympleapplication.hw9.newDesign.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mysympleapplication.hw9.newDesign.data.db.dao.*
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.*

@Database(
    entities = [UserEntity::class, SpendEntity::class, PostuplenieEntity::class, BalanceEntity::class,
        FriendsSpendsEntity::class, FriendsBalanceEntity::class, FriendsPostuplenieEntity::class],
    version = 1
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun userDao(): UserDao

    abstract fun postuplenieDao(): PostuplenieDao

    abstract fun spendDao(): SpendDao

    abstract fun balanceDao(): BalanceDao

    abstract fun friendsDao(): FriendsDao

    companion object {
        const val DB_NAME = "my_expenses.db"
    }
}