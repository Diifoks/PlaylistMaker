// data/mapper/TrackMapper.kt
package com.example.playlistmaker.data.mapper

import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.model.Track

class TrackMapper {
    fun map(trackDto: TrackDto): Track {
        return Track(
            trackName = trackDto.trackName,
            artistName = trackDto.artistName,
            trackTimeMillis = trackDto.trackTimeMillis ?: 0L,
            artworkUrl100 = trackDto.artworkUrl100,
            collectionName = trackDto.collectionName ?: "",
            releaseDate = trackDto.releaseDate?.substring(0, 4) ?: "",
            primaryGenreName = trackDto.primaryGenreName ?: "",
            country = trackDto.country ?: "",
            previewUrl = trackDto.previewUrl ?: ""
        )
    }

    fun mapList(trackDtos: List<TrackDto>): List<Track> {
        return trackDtos.map { map(it) }
    }
}