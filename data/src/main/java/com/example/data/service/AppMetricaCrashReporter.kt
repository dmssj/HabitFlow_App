package com.example.data.service

import com.example.domain.service.CrashReporter
import io.appmetrica.analytics.AppMetrica
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppMetricaCrashReporter @Inject constructor() : CrashReporter {

    override fun log(message: String) {
        AppMetrica.reportEvent(message)
    }

    override fun setKey(key: String, value: String) {
        // AppMetrica doesn't have a direct "setKey" for crash reporting like Crashlytics,
        // but we can report it as an event or via user profile if needed.
        // For this lab, we'll log it as an event.
        AppMetrica.reportEvent("set_key", mapOf(key to value))
    }

    override fun setUserId(userId: String?) {
        AppMetrica.setUserProfileID(userId)
    }

    override fun recordNonFatal(throwable: Throwable) {
        AppMetrica.reportError(throwable.message ?: "Non-fatal error", throwable)
    }
}
