package com.example.mysympleapplication.hw9.newDesign.data.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mysympleapplication.hw9.newDesign.data.db.dao.*
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        SpendEntity::class,
        PostuplenieEntity::class,
        BankCardEntity::class, // Новая таблица
        BalanceEntity::class,
        FriendsSpendsEntity::class,
        FriendsBalanceEntity::class,
        FriendsPostuplenieEntity::class,
        NameSpendsEntity::class,
        DetailsSpendEntity::class
    ],
    version = 2 // Версия 2
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun postuplenieDao(): PostuplenieDao
    abstract fun spendDao(): SpendDao
    abstract fun balanceDao(): BalanceDao
    abstract fun friendsDao(): FriendsDao
    abstract fun namesSpendsDao(): NameSpendsDao
    abstract fun detailsSpendDao(): DetailsSpendDao
    abstract fun bankCardDao(): BankCardDao

    companion object {
        const val DB_NAME = "my_expenses.db"

        @Volatile
        private var INSTANCE: AppDataBase? = null

        // --- МИГРАЦИЯ ИСПРАВЛЕНА ПОД ВАШИ ТАБЛИЦЫ ---
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 1. Создаем таблицу bank_cards
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `bank_cards` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `card_name` TEXT NOT NULL, 
                        `last_four_digits` TEXT NOT NULL, 
                        `balance` TEXT NOT NULL, 
                        `currency` TEXT NOT NULL
                    )
                    """
                )

                // 2. Создаем дефолтную карту (ID=1)
                database.execSQL(
                    """
                    INSERT INTO bank_cards (id, card_name, last_four_digits, balance, currency)
                    VALUES (1, 'Основная карта', 'Main', '0.0', 'BYN')
                    """
                )

                // 3. Пересоздаем таблицу spends (Имя таблицы изменено на `spends`)

                // А) Создаем новую временную таблицу
                // Важно: Поля spendName, value, date, card_id
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `spends_new` (
                        `id` INTEGER PRIMARY KEY NOT NULL, 
                        `spendName` TEXT NOT NULL, 
                        `value` TEXT NOT NULL, 
                        `date` TEXT NOT NULL, 
                        `card_id` TEXT NOT NULL
                    )
                    """
                )

                // Б) Копируем данные из старой `spends` в `spends_new`.
                // card_id заполняем единицей ('1').
                // url игнорируем.
                database.execSQL(
                    """
                    INSERT INTO `spends_new` (id, spendName, value, date, card_id)
                    SELECT id, spendName, value, date, '1' FROM `spends`
                    """
                )

                // В) Удаляем старую таблицу
                database.execSQL("DROP TABLE `spends`")

                // Г) Переименовываем новую таблицу обратно в `spends`
                database.execSQL("ALTER TABLE `spends_new` RENAME TO `spends`")
            }
        }

        @OptIn(DelicateCoroutinesApi::class)
        fun getDatabase(context: Context): AppDataBase {
            // Первая проверка (быстрая)
            return INSTANCE ?: synchronized(this) {
                // Вторая проверка (внутри блокировки)
                // Если INSTANCE уже инициализирован другим потоком, возвращаем его.
                // Если нет - создаем билдером и сохраняем в переменную через .also
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    DB_NAME
                )
                    .addMigrations(MIGRATION_1_2) // Ваша миграция
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Log.e("AppDataBase", "onCreate Db - First Launch")
                            GlobalScope.launch(Dispatchers.IO) {
                                // Здесь безопасно вызвать рекурсивно getDatabase, т.к. мы внутри synchronized
                                rePopulateDb(getDatabase(context))
                            }
                        }
                    })
                    .build().also {
                        INSTANCE = it
                    }
            }
        }
    }
}
