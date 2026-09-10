package com.godark14.myhabit.ui.newhabit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.godark14.myhabit.data.model.Habit
import com.godark14.myhabit.data.repository.HabitRepository
import kotlinx.coroutines.launch

private val allDays = listOf("M", "T", "W", "T", "F", "S", "S")

@Composable
fun NewHabitScreen(
    repository: HabitRepository,
    habitId: Long?,
    onHabitSaved: () -> Unit,
    onCancel: () -> Unit
) {
    val isEditing = habitId != null
    var existingHabit by remember { mutableStateOf<Habit?>(null) }
    var name by remember { mutableStateOf("") }
    var repeatEnabled by remember { mutableStateOf(false) }
    var remindersEnabled by remember { mutableStateOf(true) }
    var selectedDays by remember { mutableStateOf(setOf<Int>()) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(habitId) {
        if (habitId != null) {
            val habit = repository.getHabitById(habitId)
            existingHabit = habit
            habit?.let {
                name = it.name
                remindersEnabled = it.remindersEnabled
                selectedDays = it.repeatDays.split(",")
                    .mapNotNull { d -> d.toIntOrNull() }
                    .toSet()
                repeatEnabled = selectedDays.isNotEmpty()
            }
        }
    }

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
                text = if (isEditing) "Edit habit" else "New habit",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Row {
                if (isEditing) {
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete habit",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
                IconButton(onClick = onCancel) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Cancel")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Name your habit", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("e.g. Morning Meditations") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Repeat days", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Checkbox(checked = repeatEnabled, onCheckedChange = { repeatEnabled = it })
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            allDays.forEachIndexed { index, label ->
                val isSelected = selectedDays.contains(index)
                DayCircle(
                    label = label,
                    isSelected = isSelected,
                    onClick = {
                        selectedDays = if (isSelected) selectedDays - index else selectedDays + index
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Get reminders", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Switch(checked = remindersEnabled, onCheckedChange = { remindersEnabled = it })
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (name.isNotBlank()) {
                    coroutineScope.launch {
                        if (isEditing && existingHabit != null) {
                            repository.updateHabit(
                                existingHabit!!.copy(
                                    name = name.trim(),
                                    repeatDays = selectedDays.joinToString(","),
                                    remindersEnabled = remindersEnabled
                                )
                            )
                        } else {
                            repository.addHabit(
                                Habit(
                                    name = name.trim(),
                                    icon = "default",
                                    durationMinutes = 10,
                                    repeatDays = selectedDays.joinToString(","),
                                    remindersEnabled = remindersEnabled
                                )
                            )
                        }
                        onHabitSaved()
                    }
                }
            },
            enabled = name.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(if (isEditing) "Save Changes" else "Save Habit")
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete this habit?") },
            text = { Text("This will remove \"${existingHabit?.name}\" and its history. This can't be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    coroutineScope.launch {
                        existingHabit?.let { repository.deleteHabit(it) }
                        onHabitSaved()
                    }
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun DayCircle(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(
                if (isSelected) MaterialTheme.colorScheme.onBackground
                else MaterialTheme.colorScheme.surface,
                CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}