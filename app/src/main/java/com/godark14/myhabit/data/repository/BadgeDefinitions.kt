package com.godark14.myhabit.data.repository

import com.godark14.myhabit.data.model.Badge

object BadgeDefinitions {
    val all = listOf(
        Badge("first_habit", "First Step", "Create your first habit", "🌱"),
        Badge("five_habits", "Habit Builder", "Create 5 habits", "🏗️"),
        Badge("streak_3", "On a Roll", "Reach a 3-day streak", "🔥"),
        Badge("streak_7", "One Week Strong", "Reach a 7-day streak", "💪"),
        Badge("streak_30", "Unstoppable", "Reach a 30-day streak", "🏆"),
        Badge("ten_completions", "Consistent", "Complete habits 10 times total", "⭐"),
        Badge("fifty_completions", "Dedicated", "Complete habits 50 times total", "🎯")
    )
}