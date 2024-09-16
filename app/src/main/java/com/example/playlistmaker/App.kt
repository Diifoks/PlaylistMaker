package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    var darkTheme: Boolean = false
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        // Инициализируем SharedPreferences
        sharedPrefs = getSharedPreferences("app_preferences", MODE_PRIVATE)

        // Получаем сохранённую тему
        darkTheme = sharedPrefs.getBoolean("dark_theme", false)

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
        sharedPrefs.edit().putBoolean("dark_theme", darkTheme).apply()
    }
}