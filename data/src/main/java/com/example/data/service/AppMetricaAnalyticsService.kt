package com.example.data.service

import com.example.domain.service.AnalyticsService
import io.appmetrica.analytics.AppMetrica
import javax.inject.Inject

class AppMetricaAnalyticsService @Inject constructor() : AnalyticsService {
    override fun trackEvent(name: String, params: Map<String, Any>) {
        AppMetrica.reportEvent(name, params)
    }

    override fun trackError(message: String, error: Throwable?) {
        AppMetrica.reportError(message, error)
    }
}
