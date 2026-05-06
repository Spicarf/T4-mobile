package com.example.studentcontactapp.utils

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_DARK_MODE = "darkMode"
        private const val KEY_FONT_SIZE = "fontSize"
        private const val KEY_NOTIFICATION = "notification"
    }

    var isDarkMode: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_DARK_MODE, value).apply()

    var fontScale: Float
        get() = prefs.getFloat(KEY_FONT_SIZE, 1.0f)  // 1.0 = normal
        set(value) = prefs.edit().putFloat(KEY_FONT_SIZE, value).apply()

    var isNotificationEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATION, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIFICATION, value).apply()
}