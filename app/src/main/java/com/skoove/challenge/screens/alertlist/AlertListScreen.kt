package com.skoove.challenge.screens.alertlist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.skoove.challenge.data.response.AudioEntry
import com.skoove.challenge.storage.SharedPrefs
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

@Composable
fun AlertListScreen(action: (entry: AudioEntry) -> Unit) {
    val viewModel = koinViewModel<AlertListViewModel>()
    val isRefreshing = viewModel.uiState.collectAsState().value == AlertListUIState.Loading

    val prefs = get<SharedPrefs>()

    var favoriteEntryId by remember { mutableStateOf(prefs.getFavoriteEntryId()) }

    val pullRefreshState = rememberPullRefreshState(isRefreshing, { viewModel.getAudioEntries() })

    when (val state = viewModel.uiState.collectAsState().value) {
        is AlertListUIState.Loading, is AlertListUIState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (state == AlertListUIState.Loading) {
                    Text("Loading entries")
                } else {
                    Text("Error loading entries")
                }
            }
        }
        is AlertListUIState.Success -> {
            Box(Modifier.pullRefresh(pullRefreshState)) {
                LazyColumn {
                    items(state.entries) {
                        AudioListItem(
                            it,
                            isFavorite = it.id == favoriteEntryId,
                            onFavoriteClicked = { bool ->
                                favoriteEntryId = if (bool) {
                                    prefs.saveFavoriteEntry(it)
                                    it.id
                                } else {
                                    prefs.removeFavoriteEntry()
                                    ""
                                }
                            }
                        ) {
                            action(it)
                        }
                    }
                }

                PullRefreshIndicator(isRefreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
            }
        }
    }
}