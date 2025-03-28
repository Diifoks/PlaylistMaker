package com.example.playlistmaker.domain.model

import java.io.Serializable

data class Track(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
) : Serializable {
    fun getCoverArtwork() = artworkUrl100.replace("100x100", "512x512")
}