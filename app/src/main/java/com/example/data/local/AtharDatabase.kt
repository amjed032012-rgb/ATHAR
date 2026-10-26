package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FieldNoteEntity::class], version = 1, exportSchema = false)
abstract class AtharDatabase : RoomDatabase() {
    abstract fun fieldNoteDao(): FieldNoteDao

    companion object {
        @Volatile
        private var INSTANCE: AtharDatabase? = null

        fun getDatabase(context: Context): AtharDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AtharDatabase::class.java,
                    "athar_heritage.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
