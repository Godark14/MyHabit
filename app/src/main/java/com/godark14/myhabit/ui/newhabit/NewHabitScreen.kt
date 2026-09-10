@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.godark14.myhabit.data.model.Habit
import com.godark14.myhabit.data.repository.HabitRepository
import com.godark14.myhabit.reminder.ReminderScheduler
import kotlinx.coroutines.launch

private val allDays = listOf("M", "T", "W", "T", "F", "S", "S")

@Composable
fun NewHabitScreen(
    repository: HabitRepository,
    habitId: Long?,
    onHabitSaved: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val isEditing = habitId != null
    var existingHabit by remember { mutableStateOf<Habit?>(null) }
    var name by remember { mutableStateOf("") }
    var remindersEnabled by remember { mutableStateOf(true) }
    var reminderHour by remember { mutableStateOf(8) }
    var reminderMinute by remember { mutableStateOf(0) }
    var showReminderTimePicker by remember { mutableStateOf(false) }
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
                reminderHour = it.reminderHour ?: 8
                reminderMinute = it.reminderMinute ?: 0
                selectedDays = it.repeatDays.split(",").mapNotNull { d -> d.toIntOrNull() }.toSet()
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
                    RoundIconButton(
                        icon = Icons.Filled.Delete,
                        tint = MaterialTheme.colorScheme.error,
                        onClick = { showDeleteConfirm = true }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                RoundIconButton(icon = Icons.Filled.Close, onClick = onCancel)
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        SectionCard {
            Text(
                "Habit name",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("e.g. Morning Meditations") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SectionCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconBadge(icon = Icons.Filled.Repeat)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Repeat days",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
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
        }

        Spacer(modifier = Modifier.height(16.dp))

        SectionCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconBadge(icon = Icons.Filled.Notifications)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Get reminders",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Switch(
                    checked = remindersEnabled,
                    onCheckedChange = { remindersEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = MaterialTheme.colorScheme.secondary
                    )
                )
            }
            if (remindersEnabled) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showReminderTimePicker = true },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Reminder time",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "%02d:%02d".format(reminderHour, reminderMinute),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (name.isNotBlank()) {
                    coroutineScope.launch {
                        val savedHabit = if (isEditing && existingHabit != null) {
                            val updated = existingHabit!!.copy(
                                name = name.trim(),
                                repeatDays = selectedDays.joinToString(","),
                                remindersEnabled = remindersEnabled,
                                reminderHour = if (remindersEnabled) reminderHour else null,
                                reminderMinute = if (remindersEnabled) reminderMinute else null
                            )
                            repository.updateHabit(updated)
                            updated
                        } else {
                            val newHabit = Habit(
                                name = name.trim(),
                                icon = "default",
                                durationMinutes = 10,
                                repeatDays = selectedDays.joinToString(","),
                                remindersEnabled = remindersEnabled,
                                reminderHour = if (remindersEnabled) reminderHour else null,
                                reminderMinute = if (remindersEnabled) reminderMinute else null
                            )
                            val newId = repository.addHabit(newHabit)
                            newHabit.copy(id = newId)
                        }

                        if (remindersEnabled) {
                            ReminderScheduler.scheduleHabitReminder(context, savedHabit)
                        } else {
                            ReminderScheduler.cancelHabitReminder(context, savedHabit.id)
                        }
                        onHabitSaved()
                    }
                }
            },
            enabled = name.isNotBlank(),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Text(
                if (isEditing) "Save Changes" else "Save Habit",
                fontWeight = FontWeight.Bold
            )
        }
    }

    if (showReminderTimePicker) {
        val timePickerState = rememberTimePickerState(initialHour = reminderHour, initialMinute = reminderMinute)
        AlertDialog(
            onDismissRequest = { showReminderTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    reminderHour = timePickerState.hour
                    reminderMinute = timePickerState.minute
                    showReminderTimePicker = false
                }) { Text("Confirm") }
            },
            dismissButton = {
                TextButton(onClick = { showReminderTimePicker = false }) { Text("Cancel") }
            },
            text = { TimePicker(state = timePickerState) }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete this habit?") },
            text = { Text("This will remove \"${existingHabit?.name}\" and its history. This can't be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    coroutineScope.launch {
                        existingHabit?.let {
                            repository.deleteHabit(it)
                            ReminderScheduler.cancelHabitReminder(context, it.id)
                        }
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
private fun SectionCard(content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp), content = content)
    }
}

@Composable
private fun IconBadge(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun RoundIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun DayCircle(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .background(
                if (isSelected) MaterialTheme.colorScheme.onBackground
                else MaterialTheme.colorScheme.surfaceVariant,
                CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}