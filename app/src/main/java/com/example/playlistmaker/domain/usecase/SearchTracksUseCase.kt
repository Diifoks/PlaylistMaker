package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.repository.TrackRepository

class SearchTracksUseCase(private val repository: TrackRepository) {
    suspend fun execute(query: String) = repository.searchTracks(query)
}