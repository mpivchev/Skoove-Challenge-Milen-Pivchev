package com.skoove.challenge.data.api

import com.skoove.challenge.data.response.ApiResponse
import com.skoove.challenge.data.response.AudioEntry
import retrofit2.http.GET

interface AudioEntryApi {
    @GET("manifest.json")
    suspend fun getAudioEntries(): ApiResponse<List<AudioEntry>>
}