package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.SearchHistoryRepository

class SearchHistoryUseCase(private val repository: SearchHistoryRepository) {
    fun addTrack(track: Track) = repository.addTrack(track)
    fun getHistory() = repository.getHistory()
    fun clearHistory() = repository.clearHistory()
}