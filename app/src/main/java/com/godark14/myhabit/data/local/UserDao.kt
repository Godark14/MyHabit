package com.godark14.myhabit.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.godark14.myhabit.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM user WHERE id = 0 LIMIT 1")
    fun getUser(): Flow<User?>

    @Query("SELECT COUNT(*) FROM user")
    suspend fun getUserCount(): Int
}