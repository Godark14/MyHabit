package com.godark14.myhabit.data.repository

import android.content.Context
import androidx.glance.appwidget.updateAll
import com.godark14.myhabit.data.local.BadgeDao
import com.godark14.myhabit.data.local.HabitCompletionDao
import com.godark14.myhabit.data.local.HabitDao
import com.godark14.myhabit.data.local.UserDao
import com.godark14.myhabit.data.model.Badge
import com.godark14.myhabit.data.model.Habit
import com.godark14.myhabit.data.model.HabitCompletion
import com.godark14.myhabit.data.model.User
import com.godark14.myhabit.widget.HabitWidget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class HabitRepository(
    private val userDao: UserDao,
    private val habitDao: HabitDao,
    private val badgeDao: BadgeDao,
    private val completionDao: HabitCompletionDao,
    private val appContext: Context
) {
    // User
    fun getUser(): Flow<User?> = userDao.getUser()
    suspend fun hasUser(): Boolean = userDao.getUserCount() > 0
    suspend fun saveUser(name: String) = userDao.insertUser(User(id = 0, name = name))
    suspend fun updateUser(user: User) = userDao.updateUser(user)

    // Habits
    fun getAllHabits(): Flow<List<Habit>> = habitDao.getAllHabits()

    suspend fun getHabitById(habitId: Long): Habit? = habitDao.getHabitById(habitId)

    suspend fun addHabit(habit: Habit): Long {
        val id = habitDao.insertHabit(habit)
        seedBadgesIfNeeded()
        checkAndUnlockBadges()
        refreshWidget()
        return id
    }

    suspend fun updateHabit(habit: Habit) {
        habitDao.updateHabit(habit)
        checkAndUnlockBadges()
        refreshWidget()
    }

    suspend fun deleteHabit(habit: Habit) {
        habitDao.deleteHabit(habit)
        refreshWidget()
    }

    suspend fun toggleHabitCompletion(habit: Habit) {
        val today = LocalDate.now().toEpochDay()
        val alreadyDoneToday = completionDao.countForDay(habit.id, today) > 0

        if (alreadyDoneToday) {
            completionDao.deleteForDay(habit.id, today)
            val newStreak = (habit.streak - 1).coerceAtLeast(0)
            habitDao.updateHabit(
                habit.copy(
                    isCompletedToday = false,
                    streak = newStreak,
                    lastCompletedDate = null
                )
            )
        } else {
            completionDao.insert(HabitCompletion(habitId = habit.id, epochDay = today))
            val yesterday = today - 1
            val newStreak = if (habit.lastCompletedDate == yesterday) habit.streak + 1 else 1
            habitDao.updateHabit(
                habit.copy(
                    isCompletedToday = true,
                    streak = newStreak,
                    lastCompletedDate = today
                )
            )
            userDao.getUser().first()?.let { user ->
                userDao.updateUser(user.copy(points = user.points + 10))
            }
        }
        checkAndUnlockBadges()
        refreshWidget()
    }

    // Progrès (7 derniers jours)
    suspend fun getWeeklyCompletionRate(habit: Habit): Int {
        val today = LocalDate.now().toEpochDay()
        val weekAgo = today - 6
        val expectedDays = 7
        val done = completionDao.countForHabitInRange(habit.id, weekAgo, today)
        return ((done.toFloat() / expectedDays) * 100).toInt().coerceIn(0, 100)
    }

    suspend fun getWeeklyCompletionsCount(): Int {
        val today = LocalDate.now().toEpochDay()
        return completionDao.countAllInRange(today - 6, today)
    }

    suspend fun getBestStreak(): Int {
        return habitDao.getAllHabits().first().maxOfOrNull { it.streak } ?: 0
    }

    suspend fun getCompletedHabitIds(epochDay: Long): Set<Long> {
        return completionDao.getCompletedHabitIdsForDay(epochDay).toSet()
    }

    // Badges
    fun getAllBadges(): Flow<List<Badge>> = badgeDao.getAllBadges()

    suspend fun seedBadgesIfNeeded() {
        if (badgeDao.count() == 0) badgeDao.insertAll(BadgeDefinitions.all)
    }

    private suspend fun checkAndUnlockBadges() {
        val habits = habitDao.getAllHabits().first()
        val now = System.currentTimeMillis()

        if (habits.isNotEmpty()) badgeDao.unlockBadge("first_habit", now)
        if (habits.size >= 5) badgeDao.unlockBadge("five_habits", now)

        val maxStreak = habits.maxOfOrNull { it.streak } ?: 0
        if (maxStreak >= 3) badgeDao.unlockBadge("streak_3", now)
        if (maxStreak >= 7) badgeDao.unlockBadge("streak_7", now)
        if (maxStreak >= 30) badgeDao.unlockBadge("streak_30", now)

        val totalCompletions = completionDao.totalCount()
        if (totalCompletions >= 10) badgeDao.unlockBadge("ten_completions", now)
        if (totalCompletions >= 50) badgeDao.unlockBadge("fifty_completions", now)
    }

    private suspend fun refreshWidget() {
        HabitWidget().updateAll(appContext)
    }
}