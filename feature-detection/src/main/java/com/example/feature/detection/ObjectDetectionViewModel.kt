package com.example.feature.detection

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.DetectedObject
import com.example.domain.service.AnalyticsService
import com.example.domain.service.ObjectDetectionService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DetectionUiState {
    object Idle : DetectionUiState()
    object Loading : DetectionUiState()
    data class Success(val objects: List<DetectedObject>, val image: Bitmap) : DetectionUiState()
    object Empty : DetectionUiState()
    data class Error(val message: String) : DetectionUiState()
}

@HiltViewModel
class ObjectDetectionViewModel @Inject constructor(
    private val detectionService: ObjectDetectionService,
    private val analyticsService: AnalyticsService
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetectionUiState>(DetectionUiState.Idle)
    val uiState: StateFlow<DetectionUiState> = _uiState.asStateFlow()

    fun detectObjects(bitmap: Bitmap) {
        _uiState.value = DetectionUiState.Loading
        analyticsService.trackEvent("inference_started")
        
        viewModelScope.launch {
            try {
                val results = detectionService.detectObjects(bitmap)
                if (results.isEmpty()) {
                    _uiState.value = DetectionUiState.Empty
                } else {
                    _uiState.value = DetectionUiState.Success(results, bitmap)
                    analyticsService.trackEvent("inference_completed", mapOf("count" to results.size))
                    results.forEach { 
                        analyticsService.trackEvent("object_detected", mapOf("label" to it.label))
                    }
                }
            } catch (e: Exception) {
                _uiState.value = DetectionUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun detectObjectsFromUri(context: android.content.Context, uri: Uri) {
        _uiState.value = DetectionUiState.Loading
        analyticsService.trackEvent("image_selected")
        
        val appContext = context.applicationContext
        
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                // Ждем чуть дольше, чтобы система точно передала права
                kotlinx.coroutines.delay(300)
                
                // Читаем все байты файла сразу в память. 
                // Это самый надежный способ обойти ошибку WM lock.
                val inputStream = appContext.contentResolver.openInputStream(uri)
                val bytes = inputStream?.use { it.readBytes() }
                
                if (bytes != null) {
                    val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    if (bitmap != null) {
                        val results = detectionService.detectObjects(bitmap)
                        if (results.isEmpty()) {
                            _uiState.value = DetectionUiState.Empty
                        } else {
                            _uiState.value = DetectionUiState.Success(results, bitmap)
                            analyticsService.trackEvent("inference_completed", mapOf("count" to results.size))
                            results.forEach { 
                                analyticsService.trackEvent("object_detected", mapOf("label" to it.label))
                            }
                        }
                    } else {
                        onError("Не удалось распознать формат изображения")
                    }
                } else {
                    onError("Не удалось прочитать файл")
                }
            } catch (e: Exception) {
                onError("Ошибка доступа к фото: ${e.localizedMessage}", e)
            }
        }
    }
    
    fun onImageSelected() {
        analyticsService.trackEvent("image_selected")
    }
    
    fun onCameraOpened() {
        analyticsService.trackEvent("camera_opened")
    }

    fun onError(message: String, throwable: Throwable? = null) {
        _uiState.value = DetectionUiState.Error(message)
        analyticsService.trackError(message, throwable)
    }
}
