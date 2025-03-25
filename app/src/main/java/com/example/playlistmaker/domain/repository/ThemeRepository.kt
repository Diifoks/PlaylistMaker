// domain/repository/ThemeRepository.kt
package com.example.playlistmaker.domain.repository

interface ThemeRepository {
    fun saveTheme(isDark: Boolean)
    fun getTheme(): Boolean
}