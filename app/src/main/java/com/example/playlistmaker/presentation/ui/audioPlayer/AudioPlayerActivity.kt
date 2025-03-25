package com.example.playlistmaker.presentation.ui.audioPlayer

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isPlaying = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        // Получение данных трека из Intent
        val track = intent.getSerializableExtra("TRACK") as Track

        // Инициализация UI элементов
        val playButton = findViewById<ImageButton>(R.id.play_button)
        val backButton = findViewById<Button>(R.id.back_button)
        val songTitle = findViewById<TextView>(R.id.song_title)
        val artistName = findViewById<TextView>(R.id.artist_name)
        val songProgress = findViewById<TextView>(R.id.song_progress)

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

        // Обновление времени трека
        val updateProgress = object : Runnable {
            override fun run() {
                mediaPlayer?.let {
                    val currentPosition = it.currentPosition
                    songProgress.text = formatTrackTime(currentPosition.toLong())
                    handler.postDelayed(this, 500)
                }
            }
        }

        // Инициализация MediaPlayer
        mediaPlayer = MediaPlayer().apply {
            setDataSource(track.previewUrl)
            prepare()
            setOnCompletionListener {
                playButton.setImageResource(R.drawable.playbutton)
                this@AudioPlayerActivity.isPlaying = false
                handler.removeCallbacks(updateProgress)
                songProgress.text = "00:00"
            }
        }

        // Настройка кнопки воспроизведения
        playButton.setOnClickListener {
            if (isPlaying) {
                mediaPlayer?.pause()
                playButton.setImageResource(R.drawable.playbutton)
                handler.removeCallbacks(updateProgress)
            } else {
                mediaPlayer?.start()
                playButton.setImageResource(R.drawable.pausebutton)
                handler.post(updateProgress)
            }
            isPlaying = !isPlaying
        }

        // Настройка кнопки "Назад"
        backButton.setOnClickListener {
            stopAndReleaseMediaPlayer()
            onBackPressedDispatcher.onBackPressed()
        }
    }
    override fun onBackPressed() {
        stopAndReleaseMediaPlayer()
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAndReleaseMediaPlayer()
    }
    private fun stopAndReleaseMediaPlayer() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
    }

    // Форматирование времени
    private fun formatTrackTime(trackTimeMillis: Long): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
    }
}