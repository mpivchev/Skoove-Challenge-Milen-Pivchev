package com.skoove.challenge.storage

import android.content.Context
import android.content.SharedPreferences
import com.skoove.challenge.data.response.AudioEntry

class SharedPrefs(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveFavoriteEntry(entry: AudioEntry) {
        with(prefs.edit()) {
            putString(FAVORITE_ENTRY, entry.id)
            apply()
        }
    }

    fun removeFavoriteEntry() {
        with(prefs.edit()) {
            remove(FAVORITE_ENTRY)
            apply()
        }
    }

    fun isFavoriteEntry(entryId: String): Boolean {
        val loadedEntryId = getFavoriteEntryId()
        return entryId == loadedEntryId
    }

    fun getFavoriteEntryId(): String? {
        return prefs.getString(FAVORITE_ENTRY, null)
    }

    fun getEntryRating(audioEntryId: String): Int {
        return prefs.getInt(audioEntryId, 0)
    }

    fun saveEntryRating(audioEntryId: String, rating: Int) {
        with(prefs.edit()) {
            putInt(audioEntryId, rating)
            apply()
        }
    }

    companion object {
        const val PREFS_NAME = "prefs"
        const val FAVORITE_ENTRY = "favorite_entry"
    }
}

