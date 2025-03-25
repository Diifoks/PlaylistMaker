package com.example.playlistmaker.domain

interface ThemeManager {
    fun applyTheme(isDark: Boolean)
    val isDarkTheme: Boolean
}