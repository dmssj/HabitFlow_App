package com.example.domain.repository

import com.example.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    suspend fun saveProfile(profile: UserProfile)
    suspend fun getProfile(userId: String): UserProfile?
    fun observeProfile(userId: String): Flow<UserProfile>
    suspend fun updateFcmToken(
        userId: String,
        token: String
    )
}
