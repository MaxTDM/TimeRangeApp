package com.example.timerange.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ResultEntry::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun resultEntryDao(): ResultEntryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "result_db"
                )
                    // ※ 本サンプルではシンプルにするため allowMainThreadQueries() を使用しています。
                    // 実際のプロダクションコードでは非同期処理（AsyncTask や Coroutine）を利用してください。
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}