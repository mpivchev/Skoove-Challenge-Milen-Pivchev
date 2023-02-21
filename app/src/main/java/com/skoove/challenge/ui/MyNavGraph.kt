package com.skoove.challenge.ui

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.skoove.challenge.data.response.AudioEntry
import com.skoove.challenge.screens.alertlist.AlertListScreen
import com.skoove.challenge.screens.detail.AudioDetailScreen
import kotlinx.coroutines.InternalCoroutinesApi

object Destinations {
    const val ALERT_LIST_ROUTE = "alert_list"
    const val AUDIO_DETAIL_ROUTE = "audio_detail"

    object Arguments {
        const val AUDIO = "audio"
    }
}

@SuppressLint("UnrememberedGetBackStackEntry")
@InternalCoroutinesApi
@Composable
fun MyNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Destinations.ALERT_LIST_ROUTE
) {
    val actions = remember(navController) { MainActions(navController) }

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(Destinations.ALERT_LIST_ROUTE) {
            AlertListScreen {
                actions.navigateToAudioDetail(it)
            }
        }
        composable(
            Destinations.AUDIO_DETAIL_ROUTE,
            arguments = listOf(
                navArgument(Destinations.Arguments.AUDIO) {
                    nullable = true
                    type = NavType.ParcelableType(AudioEntry::class.java)
                })
        ) {
            val entry = navController.previousBackStackEntry?.savedStateHandle?.get<AudioEntry>(Destinations.Arguments.AUDIO)
            if (entry != null) {
                AudioDetailScreen(audio = entry)
            }
        }
    }
}

/**
 * Models the navigation actions in the app.
 */
class MainActions(navController: NavHostController) {
    val navigateToAudioDetail: (audio: AudioEntry) -> Unit = { audio ->
        navController.currentBackStackEntry?.savedStateHandle?.apply {
            set(Destinations.Arguments.AUDIO, audio)
        }
        navController.navigate(Destinations.AUDIO_DETAIL_ROUTE)
    }
}

