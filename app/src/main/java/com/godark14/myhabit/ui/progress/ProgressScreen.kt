package com.godark14.myhabit.ui.progress

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.godark14.myhabit.data.model.Habit
import com.godark14.myhabit.data.repository.HabitRepository
import com.godark14.myhabit.ui.theme.ProgressCopper
import com.godark14.myhabit.ui.theme.ProgressMaroon
import com.godark14.myhabit.ui.theme.ProgressOlive
import com.godark14.myhabit.ui.theme.ProgressPink

private val barColors = listOf(ProgressMaroon, ProgressCopper, ProgressOlive, ProgressPink)

@Composable
fun ProgressScreen(
    repository: HabitRepository,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val habits by repository.getAllHabits().collectAsState(initial = emptyList())
    val user by repository.getUser().collectAsState(initial = null)

    var habitRates by remember { mutableStateOf<Map<Long, Int>>(emptyMap()) }
    var weeklyCompletions by remember { mutableStateOf(0) }
    var bestStreak by remember { mutableStateOf(0) }

    LaunchedEffect(habits) {
        if (habits.isNotEmpty()) {
            habitRates = habits.associate { it.id to repository.getWeeklyCompletionRate(it) }
        }
        weeklyCompletions = repository.getWeeklyCompletionsCount()
        bestStreak = repository.getBestStreak()
    }

    val topHabits = habits.take(4)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your progress and insights",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(240.dp)
            )
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (topHabits.isEmpty()) {
            Text(
                text = "Complete some habits to see your progress here.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                topHabits.forEachIndexed { index, habit ->
                    ProgressBarItem(
                        label = habit.name,
                        percent = habitRates[habit.id] ?: 0,
                        color = barColors[index % barColors.size]
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Points Earned", fontWeight = FontWeight.Bold)
                        Text(
                            "All time",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        "${user?.points ?: 0} Points",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem("Best Streak", "$bestStreak days")
                    StatItem("This Week", "$weeklyCompletions done")
                    StatItem("Habits", "${habits.size} total")
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val shareText = "I've completed $weeklyCompletions habits this week on MyHabit! 🔥 Best streak: $bestStreak days."
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share your progress"))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("Share Progress")
                }
            }
        }
    }
}

@Composable
private fun ProgressBarItem(label: String, percent: Int, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxHeight()
    ) {
        Box(
            modifier = Modifier
                .width(48.dp)
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight((percent / 100f).coerceIn(0.05f, 1f))
                    .clip(RoundedCornerShape(24.dp))
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$percent%",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1
        )
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontWeight = FontWeight.Bold)
    }
}