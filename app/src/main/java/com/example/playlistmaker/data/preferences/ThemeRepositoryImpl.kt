package com.example.playlistmaker.data.preferences

import com.example.playlistmaker.data.SharedPreferencesHelper
import com.example.playlistmaker.domain.repository.ThemeRepository

class ThemeRepositoryImpl(
    private val prefsHelper: SharedPreferencesHelper
) : ThemeRepository {
    override fun saveTheme(isDark: Boolean) {
        prefsHelper.darkTheme = isDark
    }

    override fun getTheme(): Boolean = prefsHelper.darkTheme
}