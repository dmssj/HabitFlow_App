package com.example.core.service

import android.graphics.Bitmap
import com.example.domain.model.DetectedObject
import com.example.domain.service.ObjectDetectionService
import javax.inject.Inject

class FakeObjectDetectionService @Inject constructor() : ObjectDetectionService {
    var mockResults = listOf<DetectedObject>()

    override suspend fun detectObjects(bitmap: Bitmap): List<DetectedObject> {
        return mockResults
    }
}
