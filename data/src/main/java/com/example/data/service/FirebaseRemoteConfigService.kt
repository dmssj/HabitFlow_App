package com.example.data.service

import android.util.Log
import com.example.domain.service.RemoteConfigService
import com.example.domain.service.AnalyticsService
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRemoteConfigService @Inject constructor(
    private val analyticsService: AnalyticsService
) : RemoteConfigService {

    private val remoteConfig = FirebaseRemoteConfig.getInstance()

    init {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(com.example.data.R.xml.remote_config_defaults)
        
        remoteConfig.addOnConfigUpdateListener(object : com.google.firebase.remoteconfig.ConfigUpdateListener {
            override fun onUpdate(configUpdate: com.google.firebase.remoteconfig.ConfigUpdate) {
                Log.d("RemoteConfig", "Updated keys: ${configUpdate.updatedKeys}")
                remoteConfig.activate().addOnCompleteListener {
                    analyticsService.trackEvent("remote_config_updated")
                }
            }

            override fun onError(error: com.google.firebase.remoteconfig.FirebaseRemoteConfigException) {
                Log.e("RemoteConfig", "Config update error", error)
            }
        })
    }

    override suspend fun fetchAndActivate(): Boolean {
        return try {
            remoteConfig.fetchAndActivate().await()
        } catch (e: Exception) {
            Log.e("RemoteConfig", "Error fetching config", e)
            false
        }
    }

    override fun getString(key: String): String = remoteConfig.getString(key)

    override fun getBoolean(key: String): Boolean = remoteConfig.getBoolean(key)

    override fun getLong(key: String): Long = remoteConfig.getLong(key)
}
