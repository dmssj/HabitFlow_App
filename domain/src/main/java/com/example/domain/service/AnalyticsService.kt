package com.example.domain.service

interface AnalyticsService {
    fun trackEvent(
        name: String,
        params: Map<String, Any> = emptyMap()
    )

    fun trackError(
        message: String,
        error: Throwable? = null
    )
}
