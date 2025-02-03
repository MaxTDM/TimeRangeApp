package com.example.timerange.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ResultEntryDao {
    @Query("SELECT * FROM result_entries ORDER BY id DESC")
    fun getAll(): List<ResultEntry>

    @Insert
    fun insert(resultEntry: ResultEntry)
}