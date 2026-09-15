package com.godark14.myhabit.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.godark14.myhabit.data.model.HabitCompletion

@Dao
interface HabitCompletionDao {
    @Insert
    suspend fun insert(completion: HabitCompletion)

    @Query("DELETE FROM habit_completion WHERE habitId = :habitId AND epochDay = :epochDay")
    suspend fun deleteForDay(habitId: Long, epochDay: Long)

    @Query("SELECT COUNT(*) FROM habit_completion WHERE habitId = :habitId AND epochDay = :epochDay")
    suspend fun countForDay(habitId: Long, epochDay: Long): Int

    @Query("SELECT COUNT(*) FROM habit_completion WHERE habitId = :habitId AND epochDay BETWEEN :fromDay AND :toDay")
    suspend fun countForHabitInRange(habitId: Long, fromDay: Long, toDay: Long): Int

    @Query("SELECT COUNT(*) FROM habit_completion WHERE epochDay BETWEEN :fromDay AND :toDay")
    suspend fun countAllInRange(fromDay: Long, toDay: Long): Int

    @Query("SELECT COUNT(*) FROM habit_completion")
    suspend fun totalCount(): Int

    @Query("SELECT DISTINCT habitId FROM habit_completion WHERE epochDay = :epochDay")
    suspend fun getCompletedHabitIdsForDay(epochDay: Long): List<Long>
}