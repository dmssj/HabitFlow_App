package com.example.domain.service

interface CrashReporter {
    fun log(message: String)

    fun setKey(
        key: String,
        value: String
    )

    fun setUserId(userId: String?)

    fun recordNonFatal(
        throwable: Throwable
    )
}
