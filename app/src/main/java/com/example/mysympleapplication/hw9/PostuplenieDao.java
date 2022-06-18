package com.example.mysympleapplication.hw9;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mysympleapplication.hw9.model.Postuplenie;
import com.example.mysympleapplication.hw9.model.SumPostValue;

@Dao
public interface PostuplenieDao {
  @Insert
  void insert(Postuplenie postuplenie);

  @Update
  void update(Postuplenie postuplenie);


    @Query("SELECT SUM(value) as value_post FROM postuplenie GROUP BY strftime(\"%m-%Y\", date) ORDER BY date DESC")      // сумма поступлений за месяц
   SumPostValue getMonthPostuplenie();
}
