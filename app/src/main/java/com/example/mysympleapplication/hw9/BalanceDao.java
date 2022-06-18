package com.example.mysympleapplication.hw9;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mysympleapplication.hw9.model.Balance;

@Dao
public interface BalanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertB(Balance balance);

    @Update
    void updateB(Balance balance);

    @Query("SELECT * FROM balance ")
    LiveData<Balance> getBalance();

    @Query("SELECT * FROM balance ")
    Balance getBalanceSignIn();
}
