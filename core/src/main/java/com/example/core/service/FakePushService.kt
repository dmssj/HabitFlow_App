package com.example.core.service

import com.example.domain.service.PushService
import javax.inject.Inject

class FakePushService @Inject constructor() : PushService {
    private var token: String? = "fake_token"
    val subscribedTopics = mutableListOf<String>()

    override fun subscribeToTopic(topic: String) {
        subscribedTopics.add(topic)
    }

    override fun unsubscribeFromTopic(topic: String) {
        subscribedTopics.remove(topic)
    }

    override suspend fun getCurrentToken(): String? = token
}
