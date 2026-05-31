package com.example.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.UserProfile
import com.example.domain.repository.UserProfileRepository
import com.example.domain.service.AnalyticsService
import com.example.domain.service.AuthService
import com.example.domain.service.PushService
import com.example.domain.service.CrashReporter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserProfileRepository,
    private val authService: AuthService,
    private val pushService: PushService,
    private val analyticsService: AnalyticsService,
    private val crashReporter: CrashReporter
) : ViewModel() {

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile.asStateFlow()

    private val _saveSuccess = MutableSharedFlow<Unit>()
    val saveSuccess: SharedFlow<Unit> = _saveSuccess.asSharedFlow()

    init {
        try {
            crashReporter.log("ProfileViewModel init")
            crashReporter.setKey("screen", "profile")
            val user = authService.getCurrentUser()
            if (user != null) {
                crashReporter.setUserId(user.username)
                // Using username as userId for demo
                viewModelScope.launch {
                    try {
                        userRepository.observeProfile(user.username)
                            .onEach { _profile.value = it }
                            .catch { e ->
                                crashReporter.log("Failed to observe profile")
                                crashReporter.recordNonFatal(e)
                            }
                            .collect()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                
                viewModelScope.launch {
                    try {
                        val token = pushService.getCurrentToken()
                        if (token != null) {
                            userRepository.updateFcmToken(user.username, token)
                        }
                    } catch (e: Exception) {
                        crashReporter.recordNonFatal(e)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        analyticsService.trackEvent("screen_viewed", mapOf("screen_name" to "profile"))
    }

    fun saveProfile(name: String, email: String) {
        val current = _profile.value ?: UserProfile(userId = authService.getCurrentUser()?.username ?: "")
        viewModelScope.launch {
            try {
                userRepository.saveProfile(current.copy(name = name, email = email))
                _saveSuccess.emit(Unit)
            } catch (e: Exception) {
                crashReporter.log("Failed to save profile")
                crashReporter.recordNonFatal(e)
            }
        }
    }
}
