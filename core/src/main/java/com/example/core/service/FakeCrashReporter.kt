package com.example.core.service

import com.example.domain.service.CrashReporter
import javax.inject.Inject

class FakeCrashReporter @Inject constructor() : CrashReporter {
    val logs = mutableListOf<String>()
    val keys = mutableMapOf<String, String>()
    var currentUserId: String? = null
    val nonFatals = mutableListOf<Throwable>()

    override fun log(message: String) {
        logs.add(message)
    }

    override fun setKey(key: String, value: String) {
        keys[key] = value
    }

    override fun setUserId(userId: String?) {
        this.currentUserId = userId
    }

    override fun recordNonFatal(throwable: Throwable) {
        nonFatals.add(throwable)
    }
}
