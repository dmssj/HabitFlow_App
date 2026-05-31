package com.example.data.repository

import com.example.domain.model.UserProfile
import com.example.domain.repository.UserProfileRepository
import com.example.domain.service.AnalyticsService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreUserProfileRepository @Inject constructor(
    private val analyticsService: AnalyticsService
) : UserProfileRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    override suspend fun saveProfile(profile: UserProfile) {
        try {
            usersCollection.document(profile.userId).set(profile, SetOptions.merge()).await()
            analyticsService.trackEvent("firestore_profile_updated")
        } catch (e: Exception) {
            analyticsService.trackError("Failed to save profile", e)
        }
    }

    override suspend fun getProfile(userId: String): UserProfile? {
        return try {
            usersCollection.document(userId).get().await().toObject(UserProfile::class.java)
        } catch (_: Exception) {
            null
        }
    }

    override fun observeProfile(userId: String): Flow<UserProfile> = callbackFlow {
        val listener = usersCollection.document(userId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            snapshot?.toObject(UserProfile::class.java)?.let { trySend(it) }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun updateFcmToken(userId: String, token: String) {
        try {
            val update = mapOf(
                "fcmToken" to token,
                "updatedAt" to Date()
            )
            usersCollection.document(userId).set(update, SetOptions.merge()).await()
        } catch (e: Exception) {
            analyticsService.trackError("Failed to update FCM token", e)
        }
    }
}
