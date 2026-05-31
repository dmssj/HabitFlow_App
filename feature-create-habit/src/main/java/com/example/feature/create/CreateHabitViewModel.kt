package com.example.feature.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.UiState
import com.example.domain.model.Habit
import com.example.domain.usecase.CreateHabitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateHabitViewModel @Inject constructor(
    private val createHabitUseCase: CreateHabitUseCase,
    private val getHabitsUseCase: com.example.domain.usecase.GetHabitsUseCase,
    private val analyticsService: com.example.domain.service.AnalyticsService
) : ViewModel() {

    init {
        analyticsService.trackEvent("screen_viewed", mapOf("screen_name" to "create_habit"))
    }

    private val _creationState = MutableStateFlow<UiState<Long>?>(null)
    val creationState: StateFlow<UiState<Long>?> = _creationState.asStateFlow()

    fun createHabit(title: String, description: String, target: Int) {
        _creationState.value = UiState.Loading
        viewModelScope.launch {
            try {
                // Check limit for Free version
                if (!com.example.feature.create.BuildConfig.IS_PRO) {
                    val currentHabits = getHabitsUseCase().first()
                    if (currentHabits.size >= 5) {
                        _creationState.value = UiState.Error("Лимит бесплатной версии: максимум 5 привычек. Купите PRO!")
                        return@launch
                    }
                }

                val newHabit = Habit(
                    title = title,
                    description = description,
                    targetPerDay = target,
                    completedToday = 0,
                    createdAt = System.currentTimeMillis()
                )
                val id = createHabitUseCase(newHabit)
                _creationState.value = UiState.Success(id)
                analyticsService.trackEvent("habit_created", mapOf("title" to title))
            } catch (e: Exception) {
                _creationState.value = UiState.Error(e.message ?: "Failed to create habit")
            }
        }
    }

    fun resetState() {
        _creationState.value = null
    }
}
