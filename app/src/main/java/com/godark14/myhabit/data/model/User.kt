package com.godark14.myhabit.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0, // un seul utilisateur local, id fixe
    val name: String,
    val profileImageUri: String? = null,
    val points: Int = 0
)