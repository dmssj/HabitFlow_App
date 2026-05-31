package com.example.core.service

import com.example.domain.model.UserProfile
import com.example.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

class FakeUserProfileRepository @Inject constructor() : UserProfileRepository {
    private val profileFlow = MutableStateFlow<UserProfile?>(null)

    override suspend fun saveProfile(profile: UserProfile) {
        profileFlow.value = profile
    }

    override suspend fun getProfile(userId: String): UserProfile? = profileFlow.value

    override fun observeProfile(userId: String): Flow<UserProfile> = profileFlow.filterNotNull()

    override suspend fun updateFcmToken(userId: String, token: String) {
        val current = profileFlow.value ?: UserProfile(userId = userId)
        profileFlow.value = current.copy(fcmToken = token)
    }
}
