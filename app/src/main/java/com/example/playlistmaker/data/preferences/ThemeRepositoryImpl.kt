package com.example.playlistmaker.data.preferences

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.data.SharedPreferencesHelper
import com.example.playlistmaker.domain.repository.ThemeRepository

class ThemeRepositoryImpl(
    private val prefsHelper: SharedPreferencesHelper,
    private val context: Context
) : ThemeRepository {

    override fun saveTheme(isDark: Boolean) {
        prefsHelper.darkTheme = isDark
        applyTheme(isDark)
    }

    override fun getTheme(): Boolean = prefsHelper.darkTheme

    private fun applyTheme(isDark: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}