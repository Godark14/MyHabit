package com.godark14.myhabit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.godark14.myhabit.data.repository.HabitRepository
import com.godark14.myhabit.ui.navigation.NavGraph
import com.godark14.myhabit.ui.navigation.Screen
import com.godark14.myhabit.ui.theme.MyHabitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = (application as MyHabitApplication).repository
        var isReady = false
        splashScreen.setKeepOnScreenCondition { !isReady }

        setContent {
            MyHabitTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppRoot(
                        repository = repository,
                        onReady = { isReady = true },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AppRoot(
    repository: HabitRepository,
    onReady: () -> Unit,
    modifier: Modifier = Modifier
) {
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        repository.seedBadgesIfNeeded()
        val hasUser = repository.hasUser()
        startDestination = if (hasUser) Screen.Main.route else Screen.Welcome.route
        onReady()
    }

    startDestination?.let {
        NavGraph(repository = repository, startDestination = it, modifier = modifier)
    }
}