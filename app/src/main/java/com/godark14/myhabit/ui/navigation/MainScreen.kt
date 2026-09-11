package com.godark14.myhabit.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.godark14.myhabit.data.repository.HabitRepository
import com.godark14.myhabit.ui.home.HomeScreen
import com.godark14.myhabit.ui.profile.ProfileScreen
import com.godark14.myhabit.ui.progress.ProgressScreen

private enum class MainTab { HOME, PROGRESS, PROFILE }

@Composable
fun MainScreen(
    repository: HabitRepository,
    onAddHabit: () -> Unit,
    onEditHabit: (Long) -> Unit
) {
    var currentTab by remember { mutableStateOf(MainTab.HOME) }

    Box(modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)) {
        when (currentTab) {
            MainTab.HOME -> HomeScreen(
                repository = repository,
                onEditHabit = onEditHabit,
                onOpenProgress = { currentTab = MainTab.PROGRESS },
                onOpenProfile = { currentTab = MainTab.PROFILE }
            )
            MainTab.PROGRESS -> ProgressScreen(
                repository = repository,
                onClose = { currentTab = MainTab.HOME }
            )
            MainTab.PROFILE -> ProfileScreen(
                repository = repository,
                onClose = { currentTab = MainTab.HOME }
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FloatingNavBar(
                currentTab = currentTab,
                onTabSelected = { currentTab = it },
                modifier = Modifier.weight(1f)
            )
            AddHabitButton(onClick = onAddHabit)
        }
    }
}

@Composable
private fun FloatingNavBar(
    currentTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(28.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NavIcon(icon = Icons.Filled.Home, selected = currentTab == MainTab.HOME) { onTabSelected(MainTab.HOME) }
            NavIcon(icon = Icons.Filled.BarChart, selected = currentTab == MainTab.PROGRESS) { onTabSelected(MainTab.PROGRESS) }
            NavIcon(icon = Icons.Filled.Person, selected = currentTab == MainTab.PROFILE) { onTabSelected(MainTab.PROFILE) }
        }
    }
}

@Composable
private fun NavIcon(icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(if (selected) MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f) else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AddHabitButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondary)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Add habit",
            tint = MaterialTheme.colorScheme.onSecondary
        )
    }
}