package com.godark14.myhabit.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "badge")
data class Badge(
    @PrimaryKey val id: String,      // ex: "first_habit", "streak_3", "streak_7"
    val title: String,
    val description: String,
    val icon: String,                // emoji pour rester simple, pas de ressource drawable
    val isUnlocked: Boolean = false,
    val unlockedDate: Long? = null
)