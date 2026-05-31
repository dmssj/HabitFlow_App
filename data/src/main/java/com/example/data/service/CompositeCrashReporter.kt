package com.example.data.service

import com.example.domain.service.CrashReporter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompositeCrashReporter @Inject constructor(
    private val firebaseCrashReporter: FirebaseCrashReporter,
    private val appMetricaCrashReporter: AppMetricaCrashReporter
) : CrashReporter {

    private val reporters = listOf(firebaseCrashReporter, appMetricaCrashReporter)

    override fun log(message: String) {
        reporters.forEach { it.log(message) }
    }

    override fun setKey(key: String, value: String) {
        reporters.forEach { it.setKey(key, value) }
    }

    override fun setUserId(userId: String?) {
        reporters.forEach { it.setUserId(userId) }
    }

    override fun recordNonFatal(throwable: Throwable) {
        reporters.forEach { it.recordNonFatal(throwable) }
    }
}
