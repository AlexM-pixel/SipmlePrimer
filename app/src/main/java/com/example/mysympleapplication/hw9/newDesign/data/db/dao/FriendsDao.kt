package com.example.mysympleapplication.hw9.newDesign.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mysympleapplication.hw9.model.FriendsSumValue
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.FriendsBalanceEntity
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.FriendsSpendsEntity
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.FriendsSumValueEntity
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.SumSpendsOfMonthEntity

@Dao
interface FriendsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllFriendSpends(spendList: List<FriendsSpendsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBFriend(friendsBalance: FriendsBalanceEntity)

    @Query("SELECT * FROM friend_spends")
    fun getAllFriendSpends(): LiveData<List<FriendsSpendsEntity>>

    @Query("SELECT SUM(value) as value_spends," +
            "   strftime('%m-%Y', date) as dateM FROM (" +
            "   SELECT value,  date FROM spends UNION ALL" +
            "   SELECT value,  date FROM friend_spends)" +
            "   GROUP BY strftime('%m-%Y', date)" +
            "   ORDER BY date DESC")
    fun getGeneralSumMonth(): LiveData<List<SumSpendsOfMonthEntity>>

    @Query("SELECT * FROM friend_balance")
    fun getBalance(): LiveData<FriendsBalanceEntity>

    @Query("SELECT SUM(value) as value_spends,strftime(\"%m-%Y\", date) as dateM FROM friend_spends GROUP BY strftime(\"%m-%Y\", date) ORDER BY date DESC")
    fun getSumMonthFriendsSpend(): List<FriendsSumValueEntity>
}