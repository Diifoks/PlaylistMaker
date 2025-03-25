package com.example.playlistmaker.data.preferences

import com.example.playlistmaker.data.mapper.TrackMapper
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TrackRepository

class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
    private val trackMapper: TrackMapper
) : TrackRepository {

    override suspend fun searchTracks(query: String): Result<List<Track>> {
        return networkClient.searchTracks(query).map { response ->
            trackMapper.mapList(response.results)
        }
    }
}