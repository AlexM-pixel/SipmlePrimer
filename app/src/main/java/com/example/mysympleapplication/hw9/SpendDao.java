package com.example.mysympleapplication.hw9;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mysympleapplication.hw9.model.FriendsSpends;

import java.util.List;


@Dao
public interface SpendDao {
    @Insert
    void insert(Spend spend);

    @Insert
    void inserAll(List<Spend> spendList);

    @Query("SELECT * FROM spends")
    List<Spend> getAllSpends();

    @Update
    void update(Spend spend);

    @Query("DELETE FROM spends WHERE id= :id ")
    void delete(String id);

    @Query("SELECT id, value as totalValue, date, spendName FROM spends WHERE strftime(\"%m-%Y\", date)=strftime(\"%m-%Y\",:choiceDate) AND spendName= :name ORDER BY date DESC")
    LiveData<List<CalendarSpends>> getAll(String choiceDate, String name);

    @Query("SELECT SUM(value) as value_spends,strftime(\"%m-%Y\", date) as dateM FROM spends GROUP BY strftime(\"%m-%Y\", date) ORDER BY date DESC")
    LiveData<List<SumSpendsOfMonth>> getSumMonth();

    @Query("SELECT SUM(value) as value_spends,strftime(\"%m-%Y\", date) as dateM FROM spends GROUP BY strftime(\"%m-%Y\", date) ORDER BY date DESC")
    List<SumSpendsOfMonth> getSumMonthValue();

    @Query("SELECT SUM(value) as totalValue, date, spendName FROM spends WHERE strftime(\"%m-%Y\", date)=strftime( :choiceDate) GROUP BY spendName ORDER BY date DESC")
    LiveData<List<CalendarSpends>> getMonthSpends(String choiceDate);


}
