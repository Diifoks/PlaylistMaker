package com.example.playlistmaker.presentation

import android.app.Application
import com.example.playlistmaker.data.Consts
import com.example.playlistmaker.data.SharedPreferencesHelper
import com.google.gson.Gson

class App : Application() {
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
        private set

    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = getSharedPreferences(Consts.PREFS_NAME, MODE_PRIVATE)
        sharedPreferencesHelper = SharedPreferencesHelper(sharedPreferences, Gson())
    }
}