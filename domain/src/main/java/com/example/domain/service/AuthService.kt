package com.example.domain.service

import com.example.domain.model.AuthResult
import com.example.domain.model.User

interface AuthService {
    fun getYandexLoginIntent(context: Any): Any?
    suspend fun handleYandexResult(resultCode: Int, data: Any?): AuthResult
    suspend fun loginWithYandex(activity: Any): AuthResult // Keep for backward compatibility if needed, but we'll use the new methods
    fun logout()
    fun getCurrentUser(): User?
}
