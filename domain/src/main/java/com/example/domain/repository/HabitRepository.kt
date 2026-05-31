package com.example.domain.repository

import com.example.domain.model.Habit
import com.example.domain.model.HabitProgress
import com.example.domain.model.HabitStatistics
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun getHabits(): Flow<List<Habit>>
    fun getHabitById(id: Long): Flow<Habit?>
    suspend fun insertHabit(habit: Habit): Long
    suspend fun updateHabit(habit: Habit)
    suspend fun deleteHabit(habit: Habit)
    suspend fun completeHabit(habitId: Long)
}

interface StatisticsRepository {
    fun getHabitStatistics(): Flow<HabitStatistics>
}
