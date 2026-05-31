package com.example.domain.repository

import com.example.domain.model.AuthProvider

interface TokenRepository {
    fun saveToken(token: String, username: String, provider: AuthProvider)
    fun getToken(): String?
    fun getUsername(): String?
    fun getProvider(): AuthProvider?
    fun clear()
}
