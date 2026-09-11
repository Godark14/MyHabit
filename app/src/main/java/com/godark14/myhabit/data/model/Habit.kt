package com.godark14.myhabit.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habit")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String,
    val colorHex: String = "#5A3728",
    val durationMinutes: Int,
    val repeatDays: String,
    val remindersEnabled: Boolean = false,
    val reminderHour: Int? = null,
    val reminderMinute: Int? = null,
    val goalAmount: String? = null,
    val goalDate: Long? = null,
    val streak: Int = 0,
    val isCompletedToday: Boolean = false,
    val lastCompletedDate: Long? = null
)