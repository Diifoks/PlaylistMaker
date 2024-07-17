package com.example.playlistmaker

import android.content.Intent
import android.widget.Toast
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val shareImageView = findViewById<ImageView>(R.id.shareButton)
        shareImageView.setOnClickListener {
            shareApp()
        }

        val supportImageView = findViewById<ImageView>(R.id.supportButton)
        supportImageView.setOnClickListener {
            writeSupportEmail()
        }

        val agreementImageView = findViewById<ImageView>(R.id.agreementButton)
        agreementImageView.setOnClickListener {
            openUserAgreement()
        }

        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun shareApp() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text))
        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent, null))
    }
    private fun writeSupportEmail() {
        val recipient = getString(R.string.support_email)
        val subject = getString(R.string.support_email_subject)
        val body = getString(R.string.support_email_body)

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$recipient")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        startActivity(Intent.createChooser(intent,getString(R.string.text_email)))

    }
    private fun openUserAgreement() {
        val url = getString(R.string.yandex_praktikum_terms_url)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

}