package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    var darkTheme: Boolean = false
    private lateinit var sharedPrefs: SharedPreferences

    companion object {
        private const val PREFS_NAME = "app_preferences"
        private const val KEY_DARK_THEME = "dark_theme"
    }

    override fun onCreate() {
        super.onCreate()

        // Инициализируем SharedPreferences
        sharedPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        // Получаем сохранённую тему
        darkTheme = sharedPrefs.getBoolean(KEY_DARK_THEME, false)

        // Устанавливаем тему в соответствии с сохранённым состоянием
        switchTheme(darkTheme)
    }

    // Метод для переключения темы и сохранения состояния
    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        // Изменяем режим ночной темы
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )

        // Сохранение нового состояния темы
        sharedPrefs.edit().putBoolean(KEY_DARK_THEME, darkTheme).apply()
    }
}