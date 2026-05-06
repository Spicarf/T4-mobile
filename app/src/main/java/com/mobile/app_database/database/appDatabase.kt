package com.mobile.app_database.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.studentdirectory.database.dao.StudentDao
import com.example.studentdirectory.database.entity.StudentEntity

@Database(
    entities = [StudentEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao

    companion object {
        // @Volatile: pastikan INSTANCE selalu terbaca dari main memory, bukan cache CPU
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "student_database"
                )
                    .fallbackToDestructiveMigration() // untuk dev: hapus & buat ulang jika schema berubah
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}