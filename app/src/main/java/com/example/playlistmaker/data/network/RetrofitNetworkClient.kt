package com.example.playlistmaker.data.network

import android.content.Context
import com.example.playlistmaker.data.dto.SearchResponseDto
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient(context: Context) : NetworkClient {
    private val iTunesApi = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(iTunesApi::class.java)

    override suspend fun searchTracks(query: String): Result<SearchResponseDto> {
        return try {
            Result.success(iTunesApi.searchTracks(query))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}