package com.nhenia.widgtit

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Advice::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun adviceDao(): AdviceDao

    private class AppDatabaseCallback(
        private val context: Context,
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val adviceDao = database.adviceDao()
                    val hints = HintWidgetProvider.cachedStringArrays ?: context.resources.getStringArray(R.array.hints)
                    val adviceList = hints.map { Advice(text = it) }
                    adviceDao.insertAll(adviceList)
                }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "advice_database"
                )
                    .addCallback(AppDatabaseCallback(context, scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
