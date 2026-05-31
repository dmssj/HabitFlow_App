package com.example.domain.repository

import com.example.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class FakeUserProfileRepository : UserProfileRepository {
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

class UserProfileRepositoryTest {

    private val repository = FakeUserProfileRepository()

    @Test
    fun `saveProfile should correctly update current profile`() = runTest {
        val profile = UserProfile(userId = "123", name = "Test User")
        repository.saveProfile(profile)
        
        val retrieved = repository.getProfile("123")
        assertEquals("Test User", retrieved?.name)
    }

    @Test
    fun `updateFcmToken should update only token`() = runTest {
        val profile = UserProfile(userId = "123", name = "Test User")
        repository.saveProfile(profile)
        
        repository.updateFcmToken("123", "new_token")
        
        val updated = repository.getProfile("123")
        assertEquals("new_token", updated?.fcmToken)
        assertEquals("Test User", updated?.name)
    }
}
