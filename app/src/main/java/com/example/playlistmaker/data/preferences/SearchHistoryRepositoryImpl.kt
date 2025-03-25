package com.example.playlistmaker.data.preferences

import com.example.playlistmaker.data.SharedPreferencesHelper
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.SearchHistoryRepository

class SearchHistoryRepositoryImpl(
    private val prefsHelper: SharedPreferencesHelper
) : SearchHistoryRepository {
    override fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeIf { it.trackName == track.trackName }
        history.add(0, track)
        if (history.size > 10) history.removeLast()
        prefsHelper.saveSearchHistory(history)
    }

    override fun getHistory(): List<Track> = prefsHelper.loadSearchHistory()
    override fun clearHistory() = prefsHelper.saveSearchHistory(emptyList())
}