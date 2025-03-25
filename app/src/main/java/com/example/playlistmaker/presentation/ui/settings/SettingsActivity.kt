package com.example.playlistmaker.presentation.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.presentation.App
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.Creator
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private val themeUseCase by lazy { Creator.provideThemeUseCase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(
            if (themeUseCase.getCurrentTheme())
                AppCompatDelegate.MODE_NIGHT_YES
            else
                AppCompatDelegate.MODE_NIGHT_NO
        )
        setContentView(R.layout.activity_settings)

        setupClickListeners()
        setupThemeSwitcher()
    }

    private fun setupThemeSwitcher() {
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        themeSwitcher.isChecked = themeUseCase.getCurrentTheme()

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            themeUseCase.toggleTheme(isChecked)
            (application as App).applyTheme(isChecked)
            recreate()
        }
    }
    private fun setupClickListeners() {
        findViewById<ImageView>(R.id.shareButton).setOnClickListener { shareApp() }
        findViewById<ImageView>(R.id.supportButton).setOnClickListener { writeSupportEmail() }
        findViewById<ImageView>(R.id.agreementButton).setOnClickListener { openUserAgreement() }
        findViewById<Button>(R.id.backButton).setOnClickListener { finish() }
    }

    private fun shareApp() {
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text))
            startActivity(Intent.createChooser(this, null))
        }
    }

    private fun writeSupportEmail() {
        Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:${getString(R.string.support_email)}")
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_email_subject))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.support_email_body))
            startActivity(Intent.createChooser(this, getString(R.string.text_email)))
        }
    }

    private fun openUserAgreement() {
        Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(getString(R.string.yandex_praktikum_terms_url))
            startActivity(this)
        }
    }

    override fun onResume() {
        super.onResume()
        AppCompatDelegate.setDefaultNightMode(
            if (themeUseCase.getCurrentTheme()) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}