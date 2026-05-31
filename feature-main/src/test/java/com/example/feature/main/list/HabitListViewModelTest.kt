package com.example.feature.main.list

import com.example.core.service.FakeCrashReporter
import com.example.core.service.FakeAnalyticsService
import com.example.domain.usecase.CompleteHabitUseCase
import com.example.domain.usecase.DeleteHabitUseCase
import com.example.domain.usecase.GetHabitsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class HabitListViewModelTest {

    private lateinit var viewModel: HabitListViewModel
    private val getHabitsUseCase: GetHabitsUseCase = mock()
    private val completeHabitUseCase: CompleteHabitUseCase = mock()
    private val deleteHabitUseCase: DeleteHabitUseCase = mock()
    private val analyticsService = FakeAnalyticsService()
    private val crashReporter = FakeCrashReporter()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        whenever(getHabitsUseCase()).thenReturn(flowOf(emptyList()))
        viewModel = HabitListViewModel(
            getHabitsUseCase,
            completeHabitUseCase,
            deleteHabitUseCase,
            analyticsService,
            crashReporter
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should log breadcrumb and set screen key`() {
        assertTrue(crashReporter.logs.contains("HabitListViewModel init"))
        assertEquals("habit_list", crashReporter.keys["screen"])
    }

    @Test
    fun `init should track screen_viewed event`() {
        val events = analyticsService.events
        assertTrue(events.any { it.first == "screen_viewed" && it.second["screen_name"] == "habit_list" })
    }
}
