package com.example.data.repository

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.domain.model.AuthProvider
import com.example.domain.repository.TokenRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
) : TokenRepository {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_tokens",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun saveToken(token: String, username: String, provider: AuthProvider) {
        sharedPreferences.edit()
            .putString("access_token", token)
            .putString("username", username)
            .putString("provider", provider.name)
            .apply()
    }

    override fun getToken(): String? = sharedPreferences.getString("access_token", null)

    override fun getUsername(): String? = sharedPreferences.getString("username", null)

    override fun getProvider(): AuthProvider? {
        val name = sharedPreferences.getString("provider", null)
        return name?.let { AuthProvider.valueOf(it) }
    }

    override fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}
