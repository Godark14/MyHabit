package com.godark14.myhabit

import android.app.Application
import com.godark14.myhabit.data.local.AppDatabase
import com.godark14.myhabit.data.repository.HabitRepository

class MyHabitApplication : Application() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy {
        HabitRepository(
            userDao = database.userDao(),
            habitDao = database.habitDao(),
            badgeDao = database.badgeDao(),
            completionDao = database.habitCompletionDao(),
            appContext = applicationContext
        )
    }
}