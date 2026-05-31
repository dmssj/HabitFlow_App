package com.example.domain.service

interface PushService {
    fun subscribeToTopic(topic: String)
    fun unsubscribeFromTopic(topic: String)
    suspend fun getCurrentToken(): String?
}
