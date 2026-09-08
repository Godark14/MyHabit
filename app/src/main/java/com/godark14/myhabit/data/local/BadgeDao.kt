package com.godark14.myhabit.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.godark14.myhabit.data.model.Badge
import kotlinx.coroutines.flow.Flow

@Dao
interface BadgeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(badges: List<Badge>)

    @Query("SELECT * FROM badge ORDER BY isUnlocked DESC, id ASC")
    fun getAllBadges(): Flow<List<Badge>>

    @Query("SELECT * FROM badge WHERE id = :badgeId")
    suspend fun getBadgeById(badgeId: String): Badge?

    @Query("UPDATE badge SET isUnlocked = 1, unlockedDate = :date WHERE id = :badgeId AND isUnlocked = 0")
    suspend fun unlockBadge(badgeId: String, date: Long)

    @Query("SELECT COUNT(*) FROM badge")
    suspend fun count(): Int
}