package com.godark14.myhabit.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.ui.Modifier
import com.godark14.myhabit.data.repository.HabitRepository
import com.godark14.myhabit.ui.newhabit.NewHabitScreen
import com.godark14.myhabit.ui.onboarding.WelcomeScreen
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Main : Screen("main")
    object NewHabit : Screen("new_habit")
    object EditHabit : Screen("edit_habit/{habitId}") {
        fun createRoute(habitId: Long) = "edit_habit/$habitId"
    }
}

@Composable
fun NavGraph(
    repository: HabitRepository,
    startDestination: String,
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Welcome.route) {
            val coroutineScope = rememberCoroutineScope()
            WelcomeScreen(
                onNameSaved = { name ->
                    coroutineScope.launch {
                        repository.saveUser(name)
                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            MainScreen(
                repository = repository,
                onAddHabit = { navController.navigate(Screen.NewHabit.route) },
                onEditHabit = { habitId -> navController.navigate(Screen.EditHabit.createRoute(habitId)) }
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
            arguments = listOf(navArgument("habitId") { type = NavType.LongType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getLong("habitId")
            NewHabitScreen(
                repository = repository,
                habitId = habitId,
                onHabitSaved = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }
    }
}