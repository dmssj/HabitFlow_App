package com.example.data.service

import android.util.Log
import com.example.domain.service.PushService
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebasePushService @Inject constructor() : PushService {
    override fun subscribeToTopic(topic: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
            .addOnCompleteListener { task: com.google.android.gms.tasks.Task<Void> ->
                if (task.isSuccessful) {
                    Log.d("FirebasePushService", "Subscribed to $topic")
                }
            }
    }

    override fun unsubscribeFromTopic(topic: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
            .addOnCompleteListener { task: com.google.android.gms.tasks.Task<Void> ->
                if (task.isSuccessful) {
                    Log.d("FirebasePushService", "Unsubscribed from $topic")
                }
            }
    }

    override suspend fun getCurrentToken(): String? {
        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e("FirebasePushService", "Error getting token", e)
            null
        }
    }
}
