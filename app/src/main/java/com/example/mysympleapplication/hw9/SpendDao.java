package com.example.mysympleapplication.hw9;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SpendDao {
    @Insert
    void insert(Spend spend);

    @Query("SELECT value as totalValue, date, spendName FROM spends WHERE strftime(\"%m-%Y\", date)=strftime(\"%m-%Y\",:choiceDate) AND spendName= :name")
    List<CalendarSpends> getAll(String choiceDate, String name);

    @Query("SELECT SUM(value) as value_spends,strftime(\"%m-%Y\", date) as dateM FROM spends GROUP BY strftime(\"%m-%Y\", date)")
    List<SumSpendsOfMonth> getSumMonth();

    @Query("SELECT SUM(value) as totalValue, date, spendName FROM spends WHERE strftime(\"%m-%Y\", date)=strftime( :choiceDate) GROUP BY spendName")
    List<CalendarSpends> getMonthSpends(String choiceDate);

}
