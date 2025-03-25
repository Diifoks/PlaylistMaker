package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.repository.ThemeRepository

class ThemeUseCase(private val repository: ThemeRepository) {
    fun toggleTheme(isDark: Boolean) = repository.saveTheme(isDark)
    fun getCurrentTheme() = repository.getTheme()
}