package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.SearchResponseDto

interface NetworkClient {
    suspend fun searchTracks(query: String): Result<SearchResponseDto>
}