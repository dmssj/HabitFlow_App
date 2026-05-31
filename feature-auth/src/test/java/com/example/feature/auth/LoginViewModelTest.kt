package com.example.feature.auth

import com.example.domain.model.AuthProvider
import com.example.domain.model.AuthResult
import com.example.domain.model.User
import com.example.domain.service.AuthService
import com.example.core.service.FakeAnalyticsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private val authService: AuthService = mock()
    private val analyticsService = FakeAnalyticsService()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(authService, analyticsService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loginWithYandex should track user_logged_in event on success`() = runTest {
        val user = User("Yandex User", AuthProvider.YANDEX)
        whenever(authService.loginWithYandex(any())).thenReturn(AuthResult.Success(user))

        viewModel.loginWithYandex(Any())

        val events = analyticsService.events
        assertEquals(1, events.size)
        val event = events[0]
        assertEquals("user_logged_in", event.first)
        assertEquals("yandex", event.second["provider"])
    }

    @Test
    fun `onScreenOpened should track screen_viewed event`() {
        viewModel.onScreenOpened()

        val events = analyticsService.events
        assertEquals(1, events.size)
        val event = events[0]
        assertEquals("screen_viewed", event.first)
        assertEquals("login", event.second["screen_name"])
    }
}
