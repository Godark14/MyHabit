package com.godark14.myhabit.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.godark14.myhabit.data.repository.HabitRepository
import com.godark14.myhabit.ui.home.HomeScreen
import com.godark14.myhabit.ui.newhabit.NewHabitScreen
import com.godark14.myhabit.ui.onboarding.WelcomeScreen
import com.godark14.myhabit.ui.profile.ProfileScreen
import com.godark14.myhabit.ui.progress.ProgressScreen
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Home : Screen("home")
    object NewHabit : Screen("new_habit")
    object Progress : Screen("progress")
    object Profile : Screen("profile")
}

@Composable
fun NavGraph(
    repository: HabitRepository,
    startDestination: String,
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    NavHost(
        navController = navController, startDestination = startDestination, modifier = modifier
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onNameSaved = { name ->
                    coroutineScope.launch {
                        repository.saveUser(name)
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    }
                })
        }
        composable(Screen.Home.route) {
            HomeScreen(
                repository = repository,
                onAddHabit = { navController.navigate(Screen.NewHabit.route) },
                onOpenProgress = { navController.navigate(Screen.Progress.route) },
                onOpenProfile = { navController.navigate(Screen.Profile.route) })
        }
        composable(Screen.NewHabit.route) {
            NewHabitScreen(
                repository = repository,
                onHabitSaved = { navController.popBackStack() },
                onCancel = { navController.popBackStack() })
        }
        composable(Screen.Progress.route) {
            ProgressScreen(
                repository = repository, onClose = { navController.popBackStack() })
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                repository = repository, onClose = { navController.popBackStack() })
        }
    }
}