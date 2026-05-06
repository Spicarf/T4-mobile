package com.example.studentcontactapp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.studentcontactapp.database.AppDatabase
import com.example.studentcontactapp.database.entity.StudentEntity
import com.example.studentcontactapp.utils.SettingsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val settingsManager = SettingsManager(this)
        AppCompatDelegate.setDefaultNightMode(
            if (settingsManager.isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(this@MyApplication)
            if (db.studentDao().getStudentCount() == 0) {
                db.studentDao().insertAll(
                    listOf(
                        StudentEntity(
                            name = "Raffi Fatthoni",
                            nim = "F1D02310133",
                            prodi = "Informatika",
                            email = "f1d02310133@student.unram.ac.id",
                            semester = 6
                        )
                    )
                )
            }
        }
    }
}