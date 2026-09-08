package com.godark14.myhabit.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habit")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String,              // nom d'icône ou emoji utilisé pour l'affichage
    val durationMinutes: Int,
    val repeatDays: String,        // ex: "M,T,W" -> on parsera en liste
    val remindersEnabled: Boolean = false,
    val goalAmount: String? = null,
    val goalDate: Long? = null,    // timestamp epoch, nullable
    val streak: Int = 0,
    val isCompletedToday: Boolean = false,
    val lastCompletedDate: Long? = null // pour savoir si le streak doit être reset
)