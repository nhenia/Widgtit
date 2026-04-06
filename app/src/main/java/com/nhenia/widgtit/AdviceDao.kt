package com.nhenia.widgtit

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AdviceDao {
    @Query("SELECT * FROM advice_table")
    suspend fun getAllAdvice(): List<Advice>

    @Insert
    suspend fun insert(advice: Advice)

    @Insert
    suspend fun insertAll(advice: List<Advice>)

    @Delete
    suspend fun delete(advice: Advice)
}
