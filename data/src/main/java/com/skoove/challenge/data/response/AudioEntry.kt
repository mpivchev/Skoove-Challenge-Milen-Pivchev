package com.skoove.challenge.data.response

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * Audio object
 */
@Serializable
@Parcelize
data class AudioEntry(
    val title: String,
    val audio: String,
    val cover: String,
    val totalDurationMs: Int
) : Parcelable {
    @IgnoredOnParcel val id = title + audio + cover + totalDurationMs //Since the API entries don't have a unique ID, I make my own as unique as possible
}