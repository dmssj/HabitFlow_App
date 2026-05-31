package com.example.feature.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.UiState
import com.example.domain.model.HabitStatistics
import com.example.domain.usecase.GetHabitStatisticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HabitStatisticsViewModel @Inject constructor(
    private val getHabitStatisticsUseCase: GetHabitStatisticsUseCase,
    private val analyticsService: com.example.domain.service.AnalyticsService
) : ViewModel() {

    init {
        analyticsService.trackEvent("screen_viewed", mapOf("screen_name" to "statistics"))
    }

    val uiState: StateFlow<UiState<HabitStatistics>> = getHabitStatisticsUseCase()
        .map { UiState.Success(it) as UiState<HabitStatistics> }
        .catch { emit(UiState.Error(it.message ?: "Failed to load statistics")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )
}
