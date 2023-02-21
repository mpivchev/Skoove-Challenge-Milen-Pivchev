package com.skoove.challenge.data.repository

import com.skoove.challenge.data.api.AudioEntryApi
import com.skoove.challenge.data.response.ApiResponse
import com.skoove.challenge.data.response.AudioEntry

interface AudioEntryRepository {
    suspend fun getAudioEntries(): ApiResponse<List<AudioEntry>>
}

class AudioEntryRepositoryImpl(private val api: AudioEntryApi): AudioEntryRepository {
    override suspend fun getAudioEntries() = api.getAudioEntries()
}