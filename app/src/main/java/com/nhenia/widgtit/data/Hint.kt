package com.nhenia.widgtit.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hints")
data class Hint(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text: String
)
