package com.skoove.challenge.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.skoove.challenge.R
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
@Composable
fun MyApp() {
    MaterialTheme {
        val navController = rememberNavController()
        val scaffoldState = rememberScaffoldState()

        var canPop by remember { mutableStateOf(false) }

        navController.addOnDestinationChangedListener { controller, _, _ ->
            canPop = controller.previousBackStackEntry != null
        }

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(title = { Text(text = "Skoovin'", textAlign = TextAlign.Center) }, navigationIcon = {
                    if (canPop) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back_button)
                            )
                        }
                    }

                })
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding))
            {
                MyNavGraph(navController = navController)
            }
        }
    }
}
