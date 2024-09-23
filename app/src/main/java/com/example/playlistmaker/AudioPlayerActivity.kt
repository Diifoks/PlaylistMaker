package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        // Получение данных трека из Intent
        val track = intent.getSerializableExtra("TRACK") as Track

        // Инициализация UI элементов
        val backButton = findViewById<Button>(R.id.back_button)
        val songTitle = findViewById<TextView>(R.id.song_title)
        val artistName = findViewById<TextView>(R.id.artist_name)
        val songDetailsDuration = findViewById<TextView>(R.id.song_details_duration)
        val songDetailsAlbum = findViewById<TextView>(R.id.song_details_album)
        val songDetailsYear = findViewById<TextView>(R.id.song_details_year)
        val songDetailsGenre = findViewById<TextView>(R.id.song_details_genre)
        val songDetailsCountry = findViewById<TextView>(R.id.song_details_country)

        // Установка значений трекаы
        songTitle.text = track.trackName
        artistName.text = track.artistName
        songDetailsDuration.text = formatTrackTime(track.trackTimeMillis)
        songDetailsAlbum.text = track.collectionName ?: "Не указано"
        songDetailsYear.text = track.releaseDate?.substring(0, 4) ?: "Не указано"
        songDetailsGenre.text = track.primaryGenreName ?: "Не указано"
        songDetailsCountry.text = track.country ?: "Не указано"

        // Обработка обложки альбома
        Glide.with(this)
            .load(track.getCoverArtwork()) // Используем метод для получения URL
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .centerInside()
            .transform(RoundedCorners(10))
            .into(findViewById(R.id.album_cover))

        // Обработка кнопки "Назад"
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Обработка нажатия кнопки назад
        }
    }


    // Форматирование времени
    private fun formatTrackTime(trackTimeMillis: Long): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
    }
}