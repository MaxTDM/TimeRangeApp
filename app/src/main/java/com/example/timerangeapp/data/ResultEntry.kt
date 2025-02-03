package com.example.timerange.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "result_entries")
data class ResultEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val startTime: Int,
    val endTime: Int,
    val checkTime: Int,
    val isIncluded: Boolean
)