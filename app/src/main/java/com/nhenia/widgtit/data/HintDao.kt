package com.nhenia.widgtit.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HintDao {
    @Query("SELECT * FROM hints")
    suspend fun getAll(): List<Hint>

    @Insert
    suspend fun insert(hint: Hint)

    @Delete
    suspend fun delete(hint: Hint)
}
