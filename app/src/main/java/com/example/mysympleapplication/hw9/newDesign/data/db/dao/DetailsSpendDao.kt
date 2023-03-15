package com.example.mysympleapplication.hw9.newDesign.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.DetailsSpendEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DetailsSpendDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetailBySpend(detail: DetailsSpendEntity)

    @Query("SELECT * FROM detailsSpend WHERE fKey= :idSpend")
    fun getDetailsBySpendFlow(idSpend: Long): Flow<List<DetailsSpendEntity>>

    @Query("SELECT * FROM detailsSpend")
  suspend  fun getAllDetails():List<DetailsSpendEntity>
}