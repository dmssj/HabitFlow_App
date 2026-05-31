package com.example.domain.model

data class Habit(
    val id: Long = 0,
    val title: String,
    val description: String,
    val targetPerDay: Int,
    val completedToday: Int, // Number of times completed today
    val createdAt: Long = System.currentTimeMillis()
)

data class HabitProgress(
    val id: Long = 0,
    val habitId: Long,
    val completedAt: Long = System.currentTimeMillis()
)

data class HabitStatistics(
    val totalHabits: Int,
    val completedHabitsCount: Int,
    val averageCompletionRate: Float, // percentage out of 100
    val streakDays: Int, // longest streak of doing any habit
    val completionsByDayOfWeek: Map<Int, Int> // day of week (1..7) -> count
)
