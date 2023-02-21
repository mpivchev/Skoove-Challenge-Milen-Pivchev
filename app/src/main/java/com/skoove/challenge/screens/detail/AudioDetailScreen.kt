package com.skoove.challenge.screens.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.skoove.challenge.R
import com.skoove.challenge.component.FavoriteElement
import com.skoove.challenge.component.RatingStars
import com.skoove.challenge.data.response.AudioEntry
import com.skoove.challenge.storage.SharedPrefs
import com.skoove.challenge.utils.extension.timeStampToDuration
import com.skydoves.landscapist.ShimmerParams
import com.skydoves.landscapist.coil.CoilImage
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

/**
 * Audio item for the detail view
 */
@Composable
fun AudioDetailScreen(audio: AudioEntry) {
    val mediaPlayer = koinViewModel<MediaPlayerController>()

    val prefs = get<SharedPrefs>()

    var isAudioPlaying by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(prefs.isFavoriteEntry(audio.id)) }
    var playingTimeMs by remember { mutableStateOf(0) }
    val durationMs = audio.totalDurationMs
    var rating by remember { mutableStateOf(prefs.getEntryRating(audio.id)) }

    when (mediaPlayer.mediaPlayerState.collectAsState().value) {
        MediaPlayerState.Paused, MediaPlayerState.Finished, MediaPlayerState.Initialized -> {
            isAudioPlaying = false
        }
        MediaPlayerState.Started -> {
            isAudioPlaying = true
        }
        else -> {}
    }

    playingTimeMs = mediaPlayer.playingTimeMs.collectAsState().value

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    mediaPlayer.audioSelected(audio.audio)
                },
            contentAlignment = Alignment.Center
        ) {

            // Audio Cover
            CoilImage(
                imageModel = audio.cover,
                contentDescription = null,
                shimmerParams = ShimmerParams(
                    baseColor = MaterialTheme.colors.background,
                    highlightColor = MaterialTheme.colors.surface
                ),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxWidth()
            )

            // Media Player Controller Icons
            Image(
                painter = painterResource(id = if (isAudioPlaying) R.drawable.ic_pause else R.drawable.ic_play),
                contentDescription = stringResource(id = R.string.contentDescription_audio_is_favorite),
                modifier = Modifier.size(120.dp)
            )

            // audio favorite status element
            FavoriteElement(modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
                favoriteState = isFavorite,
                onClick = { bool ->
                    isFavorite = if (bool) {
                        prefs.saveFavoriteEntry(audio)
                        true
                    } else {
                        prefs.removeFavoriteEntry()
                        false
                    }
                })
        }

        Spacer(modifier = Modifier.size(32.dp))

        // Time
        Text(
            modifier = Modifier.wrapContentWidth(),
            textAlign = TextAlign.Center,
            text = "${playingTimeMs.timeStampToDuration()} / ${durationMs.timeStampToDuration()}",
            color = MaterialTheme.colors.onSurface
        )

        var sliderValue by remember { mutableStateOf(0f) }
        var isChanging by remember { mutableStateOf(false) }

        // Audio Slider
        Slider(
            value = if (isChanging) sliderValue else playingTimeMs.toFloat(),
            enabled = true,
            onValueChange = {
                isChanging = true
                sliderValue = it
            },
            valueRange = 0f..durationMs.toFloat(),
            onValueChangeFinished = {
                isChanging = false
                mediaPlayer.seekMediaPlayer((sliderValue.toInt()))
            },
            steps = 1000,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colors.secondary,
                activeTickColor = MaterialTheme.colors.secondary,
                inactiveTickColor = MaterialTheme.colors.onError,
            )
        )


        Spacer(modifier = Modifier.size(32.dp))

        // Rating
        RatingStars(modifier = Modifier.padding(8.dp),
            rating = rating,
            starSize = 64,
            onStarClicked = { value ->
                rating = value
                prefs.saveEntryRating(audio.id, rating)
            })
    }

    DisposableEffect(key1 = mediaPlayer) {
        onDispose { mediaPlayer.releaseMediaPlayer() }
    }
}
