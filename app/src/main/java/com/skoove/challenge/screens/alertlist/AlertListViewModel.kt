package com.skoove.challenge.screens.alertlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skoove.challenge.data.repository.AudioEntryRepository
import com.skoove.challenge.data.response.AudioEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlertListViewModel(private val repository: AudioEntryRepository): ViewModel() {
    private val _uiState = MutableStateFlow<AlertListUIState>(AlertListUIState.Loading)
    val uiState: StateFlow<AlertListUIState> = _uiState

    init {
        getAudioEntries()
    }

    fun getAudioEntries() {
        viewModelScope.launch {
            _uiState.update { AlertListUIState.Loading }

            val entries = repository.getAudioEntries()

            _uiState.update { AlertListUIState.Success(entries.data ?: emptyList()) }
        }
    }
}

// Represents different states for the LatestNews screen
sealed class AlertListUIState {
    object Loading : AlertListUIState()
    data class Success(val entries: List<AudioEntry>): AlertListUIState()
    data class Error(val exception: Throwable): AlertListUIState()
}