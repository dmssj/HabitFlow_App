package com.example.domain.usecase

import com.example.domain.repository.HabitRepository
import javax.inject.Inject

class CompleteHabitUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(habitId: Long) = repository.completeHabit(habitId)
}
