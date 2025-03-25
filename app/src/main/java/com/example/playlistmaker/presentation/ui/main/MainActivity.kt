package com.example.playlistmaker.presentation.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.Creator
import com.example.playlistmaker.presentation.ui.settings.SettingsActivity
import com.example.playlistmaker.presentation.ui.searchs.SearchActivity
import com.example.playlistmaker.presentation.ui.mediaLibrary.MediaLibraryActivity

class MainActivity : AppCompatActivity() {
    private val themeUseCase by lazy { Creator.provideThemeUseCase(this) }
    private var currentTheme: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        applyCurrentTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupButtons()
    }

    override fun onResume() {
        super.onResume()
        if (currentTheme != themeUseCase.getCurrentTheme()) {
            recreate()
        }
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.searchButton).setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        findViewById<Button>(R.id.mediaLibraryButton).setOnClickListener {
            startActivity(Intent(this, MediaLibraryActivity::class.java))
        }

        findViewById<Button>(R.id.settingsButton).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun applyCurrentTheme() {
        currentTheme = themeUseCase.getCurrentTheme()
        val newMode = if (currentTheme) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }

        if (AppCompatDelegate.getDefaultNightMode() != newMode) {
            AppCompatDelegate.setDefaultNightMode(newMode)
        }
    }
}