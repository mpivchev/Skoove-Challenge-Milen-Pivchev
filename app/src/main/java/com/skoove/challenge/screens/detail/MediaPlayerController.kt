package com.skoove.challenge.screens.detail

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Media player controller wrapped as a ViewModel exposing its current state
 */
class MediaPlayerController : ViewModel() {

    // object of media player
    private val mediaPlayer = MediaPlayer()

    // sealed class for handling different media player states
    private val _mediaPlayerState = MutableStateFlow<MediaPlayerState>(MediaPlayerState.None)
    val mediaPlayerState = _mediaPlayerState.asStateFlow()

    // sealed class for handling different media player states
    private val _playingTimeMs = MutableStateFlow(0)
    val playingTimeMs = _playingTimeMs.asStateFlow()

    // Media player attributes
    private val attributes = AudioAttributes
        .Builder()
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()

    private val handler = Handler(Looper.getMainLooper())

    private var playingTimeRunnable = object : Runnable {
        override fun run() {
            _playingTimeMs.value = mediaPlayer.currentPosition
            handler.postDelayed(this, 200)
        }
    }

    /**
     * Media player click handler
     */
    fun audioSelected(url: String) {
        when (mediaPlayerState.value) {
            MediaPlayerState.Started -> pauseMediaPlayer()
            MediaPlayerState.Paused, MediaPlayerState.Initialized, MediaPlayerState.Finished -> startMediaPlayer()
            else -> {
                initializeMediaPlayer(url)
                startMediaPlayer()
            }
        }
    }

    /**
     * Initialize media player with given url
     */
    private fun initializeMediaPlayer(url: String) {
        try {
            mediaPlayer.setAudioAttributes(attributes)
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepare()
            _mediaPlayerState.update { MediaPlayerState.Initialized }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Start media player
     */
    private fun startMediaPlayer() {
        try {
            mediaPlayer.start()
            _mediaPlayerState.update { MediaPlayerState.Started }
            playingTimeRunnable.run()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Pause media player
     */
    private fun pauseMediaPlayer() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            _mediaPlayerState.update { MediaPlayerState.Paused }
        }
    }


    /**
     * Release media player
     */
    fun releaseMediaPlayer() {
        mediaPlayer.stop()
        mediaPlayer.release()
        handler.removeCallbacksAndMessages(null)
        _mediaPlayerState.update { MediaPlayerState.None }

    }

    /**
     * Seek to new position
     */
    fun seekMediaPlayer(newPosition: Int) {
        mediaPlayer.seekTo(newPosition)
    }

    fun pausePositionUpdate() {
        handler.removeCallbacksAndMessages(null)
    }

    fun resumePositionUpdate() {
        playingTimeRunnable.run()
    }
}

/**
 * Media player state
 *
 * @constructor Create empty Media player state
 */
sealed class MediaPlayerState {
    object None : MediaPlayerState()
    object Initialized : MediaPlayerState()
    object Started : MediaPlayerState()
    object Paused : MediaPlayerState()
    object Finished : MediaPlayerState()
}