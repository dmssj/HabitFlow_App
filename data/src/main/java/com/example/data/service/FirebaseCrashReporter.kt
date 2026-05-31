package com.example.data.service

import android.util.Log
import com.example.domain.service.CrashReporter
import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseCrashReporter @Inject constructor() : CrashReporter {

    private val crashlytics = FirebaseCrashlytics.getInstance()

    override fun log(message: String) {
        Log.d("CrashReporter", "Firebase: logging message: $message")
        crashlytics.log(message)
    }

    override fun setKey(key: String, value: String) {
        Log.d("CrashReporter", "Firebase: setting key $key = $value")
        crashlytics.setCustomKey(key, value)
    }

    override fun setUserId(userId: String?) {
        Log.d("CrashReporter", "Firebase: setting userId: $userId")
        userId?.let { crashlytics.setUserId(it) }
    }

    override fun recordNonFatal(throwable: Throwable) {
        Log.d("CrashReporter", "Firebase: recording non-fatal: ${throwable.message}")
        crashlytics.recordException(throwable)
    }
}
