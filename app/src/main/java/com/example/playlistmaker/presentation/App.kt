package com.example.playlistmaker.presentation

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.data.SharedPreferencesHelper
import com.example.playlistmaker.data.Consts
import com.example.playlistmaker.domain.ThemeManager
import com.google.gson.Gson

class App : Application(), ThemeManager {
    val themeManager: ThemeManager = this
    private lateinit var sharedPrefsHelper: SharedPreferencesHelper

    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = getSharedPreferences(Consts.PREFS_NAME, MODE_PRIVATE)
        sharedPrefsHelper = SharedPreferencesHelper(sharedPreferences, Gson())
        applyTheme(sharedPrefsHelper.darkTheme)
    }

    override fun applyTheme(isDark: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    override val isDarkTheme: Boolean
        get() = sharedPrefsHelper.darkTheme
}