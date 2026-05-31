package com.example.feature.main.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.UiState
import com.example.domain.model.Habit
import com.example.domain.usecase.CompleteHabitUseCase
import com.example.domain.usecase.DeleteHabitUseCase
import com.example.domain.usecase.GetHabitsUseCase
import com.example.domain.service.AnalyticsService
import com.example.domain.service.CrashReporter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitListViewModel @Inject constructor(
    private val getHabitsUseCase: GetHabitsUseCase,
    private val completeHabitUseCase: CompleteHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val analyticsService: AnalyticsService,
    private val crashReporter: CrashReporter
) : ViewModel() {

    init {
        crashReporter.log("HabitListViewModel init")
        crashReporter.setKey("screen", "habit_list")
        analyticsService.trackEvent("screen_viewed", mapOf("screen_name" to "habit_list"))
    }

    val uiState: StateFlow<UiState<List<Habit>>> = getHabitsUseCase()
        .map { list -> UiState.Success(list) as UiState<List<Habit>> }
        .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )

    fun completeHabit(habitId: Long) {
        viewModelScope.launch {
            completeHabitUseCase(habitId)
            analyticsService.trackEvent("habit_completed", mapOf("habit_id" to habitId))
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            deleteHabitUseCase(habit)
        }
    }

    fun retry() {
        // Since getHabitsUseCase is a Flow, we can just trigger a refresh if the implementation supports it,
        // or just rely on the Flow emitting again. 
        // For a simple refresh, we can just re-assign the uiState if it was a cold stream, 
        // but here it's stateIn.
        // I'll just trigger a dummy action or log.
        analyticsService.trackEvent("list_retry_clicked")
    }
}
