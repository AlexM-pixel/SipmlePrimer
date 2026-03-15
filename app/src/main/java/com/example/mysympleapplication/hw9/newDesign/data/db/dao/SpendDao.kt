package com.example.mysympleapplication.hw9.newDesign.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.SpendEntity
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.SumSpendsOfMonthEntity
import com.example.mysympleapplication.hw9.newDesign.domain.model.MonthStatDto
import com.example.mysympleapplication.hw9.newDesign.domain.model.PlaceStatDto
import kotlinx.coroutines.flow.Flow

@Dao
interface SpendDao {
    @Insert
    suspend fun insert(spend: SpendEntity)

    @Insert
    suspend fun insertAllSpends(spendList: List<SpendEntity>)

    @Query("SELECT * FROM spends")
    suspend fun getAllSpends(): List<SpendEntity>

    @Query("SELECT * FROM spends WHERE id= :id")
    suspend fun getSpend(id: String?): SpendEntity

    @Update
    suspend fun update(spend: SpendEntity)

    @Query("DELETE FROM spends WHERE id= :id")
    suspend fun delete(id: String?)

    @Query("SELECT id, value , date, spendName, card_id FROM spends WHERE strftime(\'%m-%Y\', date)=strftime(\'%m-%Y\',:choiceDate) AND spendName= :name ORDER BY date DESC")
    fun getDetailSpendsByName(name: String?, choiceDate: String?): Flow<List<SpendEntity>>

    @Query("SELECT SUM(value) as value_spends,strftime(\'%m-%Y\', date) as dateM FROM spends GROUP BY strftime(\'%m-%Y\', date) ORDER BY date DESC")
    suspend fun getSumMonth(): List<SumSpendsOfMonthEntity>

    @Query("SELECT id, SUM(value) as value, date, spendName, card_id FROM spends WHERE strftime(\'%m-%Y\', date)=strftime( :choiceDate) GROUP BY spendName ORDER BY date DESC")
    suspend fun getMonthSpends(choiceDate: String): List<SpendEntity>

    @Query("SELECT SUM(value) as value_spends, strftime('%m-%Y', date) as dateM  FROM spends WHERE strftime(\'%m-%Y\', date)=strftime(\'%m-%Y\',:currentDate) GROUP BY strftime(\'%m-%Y\', date) ORDER BY date DESC LIMIT 1")
    suspend fun getCurrentMonthExpenses(currentDate:String): SumSpendsOfMonthEntity?

    @Query("SELECT SUM(value) FROM spends WHERE strftime('%m-%Y', date) = strftime('%m-%Y', 'now')")
    fun getTotalSpentThisMonth(): Flow<Double?>


    // 1. Статистика по МЕСТАМ за конкретный месяц (например, "02-2026")
    // Используется для PieChart и нижнего списка
    @Query(" SELECT spendName as name, SUM(value) as total FROM spends WHERE strftime('%m-%Y', date) = :monthYear GROUP BY spendName ORDER BY total DESC")
    fun getPlaceStatsByMonth(monthYear: String): Flow<List<PlaceStatDto>>

    // 2. Статистика по МЕСЯЦАМ за конкретный год (например, "2026")
    // Используется для LineChart (Волна)
    @Query(" SELECT strftime('%m', date) as month, SUM(value) as total FROM spends WHERE strftime('%Y', date) = :year GROUP BY month ORDER BY month ASC")
    fun getYearStats(year: String): Flow<List<MonthStatDto>>

    // Статистика по МЕСТАМ за конкретный Год
    @Query(" SELECT spendName as name, SUM(value) as total FROM spends WHERE strftime('%Y', date) = :year GROUP BY spendName ORDER BY total DESC")
    fun getPlaceStatsByYear(year: String): Flow<List<PlaceStatDto>>

    // Получаем год самой первой записи в таблице (Минимальный год)
    @Query("SELECT MIN(strftime('%Y', date)) FROM spends")
    suspend fun getFirstTransactionYear(): String?

    // Получить все покупки в конкретном месте за конкретный год
    @Query(" SELECT * FROM spends WHERE spendName = :placeName AND strftime('%Y', date) = :year ORDER BY date DESC")
    fun getHistoryForPlace(placeName: String, year: String): Flow<List<SpendEntity>>

    // Получаем все покупки в конкретном месте (сортируем по дате)
    @Query("SELECT * FROM spends WHERE spendName = :placeName ORDER BY date ASC")
    fun getSpendsByPlace(placeName: String): Flow<List<SpendEntity>>
}