package com.example.mysympleapplication.hw9.newDesign.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.BankCardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BankCardDao {

    // Добавить новую карту (если такая уже есть, заменить)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: BankCardEntity)

    // Обновить баланс карты
    @Update
    suspend fun updateCard(card: BankCardEntity)

    // Получить список всех карт (для UI, чтобы листать их)
    // Используем Flow, чтобы UI обновлялся сам при изменении баланса
    @Query("SELECT * FROM bank_cards")
    fun getAllCards(): Flow<List<BankCardEntity>>

    // Найти карту по последним 4 цифрам (нужно для BankSmsService!)
    @Query("SELECT * FROM bank_cards WHERE last_four_digits = :digits LIMIT 1")
    suspend fun getCardByDigits(digits: String): BankCardEntity?
}