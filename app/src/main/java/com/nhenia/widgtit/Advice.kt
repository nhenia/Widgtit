package com.nhenia.widgtit

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "advice_table")
data class Advice(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text: String
)
