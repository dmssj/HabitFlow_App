package com.example.domain.usecase

import com.example.domain.model.HabitStatistics
import com.example.domain.repository.StatisticsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHabitStatisticsUseCase @Inject constructor(
    private val repository: StatisticsRepository
) {
    operator fun invoke(): Flow<HabitStatistics> = repository.getHabitStatistics()
}
