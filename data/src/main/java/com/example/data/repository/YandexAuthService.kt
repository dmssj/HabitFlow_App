package com.example.data.repository

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.example.domain.model.AuthProvider
import com.example.domain.model.AuthResult
import com.example.domain.model.User
import com.example.domain.repository.TokenRepository
import com.example.domain.service.AuthService
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthResult
import com.yandex.authsdk.YandexAuthSdk
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YandexAuthService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tokenRepository: TokenRepository
) : AuthService {

    private val sdk = YandexAuthSdk.create(YandexAuthOptions(context))

    override fun getYandexLoginIntent(context: Any): Intent? {
        val loginOptions = YandexAuthLoginOptions()
        return if (context is Context) {
            sdk.contract.createIntent(context, loginOptions)
        } else null
    }

    override suspend fun handleYandexResult(resultCode: Int, data: Any?): AuthResult {
        if (data !is Intent?) return AuthResult.Error("Invalid result data")
        
        return when (val result = sdk.contract.parseResult(resultCode, data)) {
            is YandexAuthResult.Success -> {
                val token = result.token
                val username = "Yandex User" // In a real app, fetch profile info
                tokenRepository.saveToken(token.value, username, AuthProvider.YANDEX)
                AuthResult.Success(User(username, AuthProvider.YANDEX))
            }
            is YandexAuthResult.Failure -> {
                AuthResult.Error(result.exception.message ?: "Yandex Auth Error")
            }
            YandexAuthResult.Cancelled -> {
                AuthResult.Error("Login cancelled")
            }
        }
    }

    override suspend fun loginWithYandex(activity: Any): AuthResult {
        return AuthResult.Error("Use intent-based flow")
    }

    override fun logout() {
        tokenRepository.clear()
    }

    override fun getCurrentUser(): User? {
        val username = tokenRepository.getUsername()
        val provider = tokenRepository.getProvider()
        return if (username != null && provider != null) {
            User(username, provider)
        } else {
            null
        }
    }
}
