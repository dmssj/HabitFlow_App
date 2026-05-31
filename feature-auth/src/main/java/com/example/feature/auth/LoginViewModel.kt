package com.example.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.AuthResult
import com.example.domain.service.AnalyticsService
import com.example.domain.service.AuthService
import com.example.domain.service.CrashReporter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authService: AuthService,
    private val analyticsService: AnalyticsService,
    private val crashReporter: CrashReporter
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthResult?>(null)
    val authState: StateFlow<AuthResult?> = _authState.asStateFlow()

    fun getYandexLoginIntent(context: Any): Any? {
        return authService.getYandexLoginIntent(context)
    }

    fun onYandexResult(resultCode: Int, data: Any?) {
        viewModelScope.launch {
            val result = authService.handleYandexResult(resultCode, data)
            _authState.value = result
            if (result is AuthResult.Success) {
                crashReporter.setUserId(result.user.username)
                analyticsService.trackEvent("login_success", mapOf("provider" to "yandex"))
            } else if (result is AuthResult.Error) {
                crashReporter.log("Yandex login error: ${result.message}")
            }
        }
    }

    fun loginWithYandex(activity: Any) {
        crashReporter.log("Starting Yandex login")
        viewModelScope.launch {
            try {
                val result = authService.loginWithYandex(activity)
                _authState.value = result
                if (result is AuthResult.Success) {
                    crashReporter.setUserId(result.user.username)
                    analyticsService.trackEvent("login_success", mapOf("provider" to "yandex"))
                } else if (result is AuthResult.Error) {
                    crashReporter.log("Yandex login error: ${result.message}")
                }
            } catch (e: Exception) {
                crashReporter.log("Failed to login with Yandex")
                crashReporter.recordNonFatal(e)
                _authState.value = AuthResult.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun onScreenOpened() {
        crashReporter.setKey("screen", "login")
        analyticsService.trackEvent("screen_viewed", mapOf("screen_name" to "login"))
    }
}
