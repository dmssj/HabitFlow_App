package com.example.feature.main.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.UiState
import com.example.domain.model.Habit
import com.example.domain.repository.HabitRepository
import com.example.domain.usecase.CompleteHabitUseCase
import com.example.domain.usecase.UpdateHabitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitDetailsViewModel @Inject constructor(
    private val repository: HabitRepository,
    private val completeHabitUseCase: CompleteHabitUseCase,
    private val updateHabitUseCase: UpdateHabitUseCase,
    private val analyticsService: com.example.domain.service.AnalyticsService
) : ViewModel() {

    private val _habitId = MutableStateFlow<Long>(-1)

    fun setHabitId(id: Long) {
        _habitId.value = id
        analyticsService.trackEvent("screen_viewed", mapOf("screen_name" to "habit_details", "habit_id" to id))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<UiState<Habit>> = _habitId
        .flatMapLatest { id ->
            if (id == -1L) {
                flowOf(null)
            } else {
                repository.getHabitById(id)
            }
        }
        .map { habit ->
            if (habit != null) {
                UiState.Success(habit)
            } else {
                UiState.Error("Habit not found")
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )

    fun completeHabit() {
        val habitIdValue = _habitId.value
        if (habitIdValue != -1L) {
            viewModelScope.launch {
                completeHabitUseCase(habitIdValue)
            }
        }
    }

    fun updateHabit(title: String, description: String, target: Int) {
        val idVal = _habitId.value
        if (idVal != -1L) {
            viewModelScope.launch {
                val state = uiState.value
                val completed = if (state is UiState.Success) state.data.completedToday else 0
                val created = if (state is UiState.Success) state.data.createdAt else System.currentTimeMillis()
                val updatedHabit = Habit(
                    id = idVal,
                    title = title,
                    description = description,
                    targetPerDay = target,
                    completedToday = completed,
                    createdAt = created
                )
                updateHabitUseCase(updatedHabit)
            }
        }
    }
}
