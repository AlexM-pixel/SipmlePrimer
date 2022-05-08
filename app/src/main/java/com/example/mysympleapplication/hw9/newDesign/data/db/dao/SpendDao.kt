package com.example.mysympleapplication.hw9.newDesign.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mysympleapplication.hw9.CalendarSpends
import com.example.mysympleapplication.hw9.Spend
import com.example.mysympleapplication.hw9.SumSpendsOfMonth
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.CalendarSpendsEntity
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.SpendEntity
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.SumSpendsOfMonthEntity

@Dao
interface SpendDao {
    @Insert
    fun insert(spend: SpendEntity)

    @Insert
   suspend fun insertAllSpends(spendList: List<SpendEntity>)

    @Query("SELECT * FROM spends")
  suspend  fun getAllSpends(): List<SpendEntity>

    @Update
    fun update(spend: SpendEntity)

    @Query("DELETE FROM spends WHERE id= :id")
    fun delete(id: String?)

    @Query("SELECT id, value as totalValue, date, spendName FROM spends WHERE strftime(\"%m-%Y\", date)=strftime(\"%m-%Y\",:choiceDate) AND spendName= :name ORDER BY date DESC")
    fun getAll(choiceDate: String?, name: String?): LiveData<List<CalendarSpendsEntity>>

    @Query("SELECT SUM(value) as value_spends,strftime(\"%m-%Y\", date) as dateM FROM spends GROUP BY strftime(\"%m-%Y\", date) ORDER BY date DESC")
    fun getSumMonth(): LiveData<List<SumSpendsOfMonthEntity>>

    @Query("SELECT SUM(value) as totalValue, date, spendName FROM spends WHERE strftime(\"%m-%Y\", date)=strftime( :choiceDate) GROUP BY spendName ORDER BY date DESC")
    fun getMonthSpends(choiceDate: String?): LiveData<List<CalendarSpendsEntity>>
}