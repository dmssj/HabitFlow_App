package com.example.data.mapper

import com.example.domain.model.Habit
import com.example.domain.model.HabitProgress
import com.example.data.entity.HabitEntity
import com.example.data.entity.HabitProgressEntity

fun HabitEntity.toDomain(): Habit = Habit(
    id = id,
    title = title,
    description = description,
    targetPerDay = targetPerDay,
    completedToday = completedToday,
    createdAt = createdAt
)

fun Habit.toEntity(): HabitEntity = HabitEntity(
    id = id,
    title = title,
    description = description,
    targetPerDay = targetPerDay,
    completedToday = completedToday,
    createdAt = createdAt
)

fun HabitProgressEntity.toDomain(): HabitProgress = HabitProgress(
    id = id,
    habitId = habitId,
    completedAt = completedAt
)
