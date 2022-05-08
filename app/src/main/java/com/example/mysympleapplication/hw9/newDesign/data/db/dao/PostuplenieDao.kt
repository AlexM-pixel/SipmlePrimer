package com.example.mysympleapplication.hw9.newDesign.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mysympleapplication.hw9.model.SumPostValue
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.PostuplenieEntity

@Dao
interface PostuplenieDao {
    @Insert
    fun insert(postuplenie: PostuplenieEntity)

    @Update
    fun update(postuplenie: PostuplenieEntity)


    @Query("SELECT SUM(value) as value_post FROM postuplenie GROUP BY strftime(\"%m-%Y\", date) ORDER BY date DESC")
    fun getMonthPostuplenie(): SumPostValue?   // сумма поступлений за месяц
}