package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.PantryItemEntity

@Database(
    entities = [PantryItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FreshCheckDatabase : RoomDatabase() {
    abstract fun pantryDao(): PantryDao

    companion object {
        @Volatile
        private var INSTANCE: FreshCheckDatabase? = null

        fun getInstance(context: Context): FreshCheckDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FreshCheckDatabase::class.java,
                    "freshcheck_local.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
