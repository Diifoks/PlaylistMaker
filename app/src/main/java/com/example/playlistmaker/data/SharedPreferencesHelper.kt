package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.domain.model.Track
import com.google.gson.Gson

class SharedPreferencesHelper(
    private val prefs: SharedPreferences,
    private val gson: Gson
) {
    var darkTheme: Boolean
        get() = prefs.getBoolean(Consts.DARK_THEME_KEY, false)
        set(value) = prefs.edit().putBoolean(Consts.DARK_THEME_KEY, value).apply()

    fun saveSearchHistory(tracks: List<Track>) {
        prefs.edit().putString(Consts.SEARCH_HISTORY_KEY, gson.toJson(tracks)).apply()
    }

    fun loadSearchHistory(): List<Track> {
        return gson.fromJson(
            prefs.getString(Consts.SEARCH_HISTORY_KEY, null),
            Array<Track>::class.java
        )?.toList() ?: emptyList()
    }
}