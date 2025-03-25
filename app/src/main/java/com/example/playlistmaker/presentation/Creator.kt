package com.example.playlistmaker.presentation

import android.content.Context
import com.example.playlistmaker.data.Consts
import com.example.playlistmaker.data.SharedPreferencesHelper
import com.example.playlistmaker.data.mapper.TrackMapper
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.preferences.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.preferences.ThemeRepositoryImpl
import com.example.playlistmaker.data.preferences.TrackRepositoryImpl
import com.example.playlistmaker.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.domain.repository.ThemeRepository
import com.example.playlistmaker.domain.repository.TrackRepository
import com.example.playlistmaker.domain.ThemeManager
import com.example.playlistmaker.domain.usecase.*
import com.google.gson.Gson

object Creator {
    private val gson = Gson()
    private val trackMapper = TrackMapper()

    fun provideSearchTracksUseCase(context: Context): SearchTracksUseCase {
        val networkClient = RetrofitNetworkClient(context)
        val trackRepository: TrackRepository = TrackRepositoryImpl(networkClient, trackMapper)
        return SearchTracksUseCase(trackRepository)
    }

    fun provideThemeUseCase(context: Context): ThemeUseCase {
        val prefs = context.getSharedPreferences(Consts.PREFS_NAME, Context.MODE_PRIVATE)
        val prefsHelper = SharedPreferencesHelper(prefs, Gson())
        val themeRepository: ThemeRepository = ThemeRepositoryImpl(prefsHelper)
        val themeManager = (context.applicationContext as App).themeManager
        return ThemeUseCase(themeRepository, themeManager)
    }

    fun provideSearchHistoryUseCase(context: Context): SearchHistoryUseCase {
        val prefs = context.getSharedPreferences(Consts.PREFS_NAME, Context.MODE_PRIVATE)
        val prefsHelper = SharedPreferencesHelper(prefs, gson)
        val historyRepository: SearchHistoryRepository = SearchHistoryRepositoryImpl(prefsHelper)
        return SearchHistoryUseCase(historyRepository)
    }
}