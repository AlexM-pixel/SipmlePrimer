package com.example.mysympleapplication.hw9;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mysympleapplication.hw9.model.Balance;
import com.example.mysympleapplication.hw9.model.FriendsBalance;
import com.example.mysympleapplication.hw9.model.FriendsSpends;
import com.example.mysympleapplication.hw9.model.FriendsSumValue;

import java.util.List;

@Dao
public interface FriendsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllFriendSpends(List<FriendsSpends> spendList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBF(FriendsBalance friendsBalance);

    @Query("SELECT * FROM friend_spends")
    LiveData<List<FriendsSpends>> getAllFriendSpends();


    @Query("SELECT SUM(value) as value_spends," +
            "   strftime('%m-%Y', date) as dateM FROM (" +
            "   SELECT value,  date FROM spends UNION ALL" +
            "   SELECT value,  date FROM friend_spends)" +
            "   GROUP BY strftime('%m-%Y', date)" +
            "   ORDER BY date DESC")
    LiveData<List<SumSpendsOfMonth>> getGeneralSumMonth();

    @Query("SELECT * FROM friend_balance ")
    LiveData<FriendsBalance> getBalance();

    @Query("SELECT SUM(value) as value_spends,strftime(\"%m-%Y\", date) as dateM FROM friend_spends GROUP BY strftime(\"%m-%Y\", date) ORDER BY date DESC")
    List<FriendsSumValue> getSumMonthFriendsSpend();
}
