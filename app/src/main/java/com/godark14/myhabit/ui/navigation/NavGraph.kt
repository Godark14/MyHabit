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
    object EditHabit : Screen("edit_habit/{habitId}") {
        fun createRoute(habitId: Long) = "edit_habit/$habitId"
    }
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
                onEditHabit = { habitId -> navController.navigate(Screen.EditHabit.createRoute(habitId)) },
                onOpenProgress = { navController.navigate(Screen.Progress.route) },
                onOpenProfile = { navController.navigate(Screen.Profile.route) }
            )
        }
        composable(Screen.NewHabit.route) {
            NewHabitScreen(
                repository = repository,
                habitId = null,
                onHabitSaved = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.EditHabit.route,
            arguments = listOf(androidx.navigation.navArgument("habitId") { type = androidx.navigation.NavType.LongType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getLong("habitId")
            NewHabitScreen(
                repository = repository,
                habitId = habitId,
                onHabitSaved = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
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