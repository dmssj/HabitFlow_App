package com.example.data.dao

import androidx.room.*
import com.example.data.entity.HabitEntity
import com.example.data.entity.HabitProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits ORDER BY createdAt DESC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE id = :id")
    fun getHabitById(id: Long): Flow<HabitEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Query("UPDATE habits SET completedToday = completedToday + 1 WHERE id = :id")
    suspend fun incrementCompletion(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: HabitProgressEntity): Long

    @Query("SELECT * FROM habit_progress ORDER BY completedAt DESC")
    fun getAllProgress(): Flow<List<HabitProgressEntity>>
}
