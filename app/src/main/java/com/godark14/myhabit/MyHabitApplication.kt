package com.godark14.myhabit

import android.app.Application
import com.godark14.myhabit.data.local.AppDatabase
import com.godark14.myhabit.data.repository.HabitRepository

class MyHabitApplication : Application() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy {
        HabitRepository(database.userDao(), database.habitDao(), database.badgeDao(), database.habitCompletionDao())
    }
}