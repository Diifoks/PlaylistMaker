package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPreferences: SharedPreferences) {
    private val gson = Gson()
    private val key = "search_history"

    // Получение истории поиска
    fun getHistory(): ArrayList<Track> {
        val json = sharedPreferences.getString(key, null)
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return if (json != null) gson.fromJson(json, type) else ArrayList()
    }

    // Добавление трека в историю
    fun addTrack(track: Track) {
        val history = getHistory()
        history.removeIf { it.trackName == track.trackName && it.artistName == track.artistName }
        history.add(0, track) // Добавление в начало списка

        // Ограничение до 10 элементов
        if (history.size > 10) history.removeAt(history.size - 1)

        saveHistory(history)
    }

    // Сохранение истории
    private fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPreferences.edit().putString(key, json).apply()
    }

    // Очистка истории
    fun clearHistory() {
        sharedPreferences.edit().remove(key).apply()
    }
}