package com.example.domain.model

import java.util.Date

data class UserProfile(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val fcmToken: String = "",
    val updatedAt: Date = Date()
)
