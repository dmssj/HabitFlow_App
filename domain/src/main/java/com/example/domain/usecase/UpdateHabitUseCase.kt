package com.example.domain.usecase

import com.example.domain.model.Habit
import com.example.domain.repository.HabitRepository
import javax.inject.Inject

class UpdateHabitUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(habit: Habit) {
        require(habit.title.isNotBlank()) { "Title cannot be empty" }
        require(habit.targetPerDay > 0) { "Target per day must be at least 1" }
        repository.updateHabit(habit)
    }
}
