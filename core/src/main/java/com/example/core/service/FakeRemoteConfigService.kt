package com.example.core.service

import com.example.domain.service.RemoteConfigService
import javax.inject.Inject

class FakeRemoteConfigService @Inject constructor() : RemoteConfigService {
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
