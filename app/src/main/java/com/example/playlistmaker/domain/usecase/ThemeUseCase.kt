package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.ThemeManager
import com.example.playlistmaker.domain.repository.ThemeRepository

class ThemeUseCase(private val repository: ThemeRepository, themeManager: ThemeManager) {
    fun toggleTheme(isDark: Boolean) {
        repository.saveTheme(isDark)
    }

    fun getCurrentTheme(): Boolean = repository.getTheme()
}