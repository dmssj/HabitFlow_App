package com.example.domain.service

import android.graphics.Bitmap
import com.example.domain.model.DetectedObject

interface ObjectDetectionService {
    suspend fun detectObjects(
        bitmap: Bitmap
    ): List<DetectedObject>
}
