package com.example.data.service

import com.example.domain.service.RemoteConfigService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class FakeRemoteConfigService : RemoteConfigService {
    var config = mutableMapOf<String, Any>(
        "welcome_message" to "Fake Welcome",
        "is_new_feature_enabled" to true,
        "max_items_per_page" to 10L
    )

    override suspend fun fetchAndActivate(): Boolean = true

    override fun getString(key: String): String = config[key]?.toString() ?: ""

    override fun getBoolean(key: String): Boolean = config[key] as? Boolean ?: false

    override fun getLong(key: String): Long = config[key] as? Long ?: 0L
}

class RemoteConfigServiceTest {

    private val remoteConfigService = FakeRemoteConfigService()

    @Test
    fun `getString should return correct value from fake config`() {
        val message = remoteConfigService.getString("welcome_message")
        assertEquals("Fake Welcome", message)
    }

    @Test
    fun `getBoolean should return correct value from fake config`() {
        val isEnabled = remoteConfigService.getBoolean("is_new_feature_enabled")
        assertEquals(true, isEnabled)
    }
}
