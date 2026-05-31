package com.example.core.service

import com.example.domain.service.AnalyticsService
import javax.inject.Inject

class FakeAnalyticsService @Inject constructor() : AnalyticsService {
    private val _events = mutableListOf<Pair<String, Map<String, Any>>>()
    val events: List<Pair<String, Map<String, Any>>> = _events

    override fun trackEvent(name: String, params: Map<String, Any>) {
        _events.add(name to params)
    }

    override fun trackError(message: String, error: Throwable?) {
        // No-op
    }
}
