package com.example.data.repository

import com.example.domain.model.Habit
import com.example.domain.repository.HabitRepository
import com.example.data.dao.HabitDao
import com.example.data.entity.HabitProgressEntity
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HabitRepositoryImpl @Inject constructor(
    private val dao: HabitDao
) : HabitRepository {

    override fun getHabits(): Flow<List<Habit>> =
        dao.getAllHabits().map { list -> list.map { it.toDomain() } }

    override fun getHabitById(id: Long): Flow<Habit?> =
        dao.getHabitById(id).map { it?.toDomain() }

    override suspend fun insertHabit(habit: Habit): Long =
        dao.insertHabit(habit.toEntity())

    override suspend fun updateHabit(habit: Habit) {
        dao.updateHabit(habit.toEntity())
    }

    override suspend fun deleteHabit(habit: Habit) {
        dao.deleteHabit(habit.toEntity())
    }

    override suspend fun completeHabit(habitId: Long) {
        dao.incrementCompletion(habitId)
        dao.insertProgress(
            HabitProgressEntity(habitId = habitId, completedAt = System.currentTimeMillis())
        )
    }
}
