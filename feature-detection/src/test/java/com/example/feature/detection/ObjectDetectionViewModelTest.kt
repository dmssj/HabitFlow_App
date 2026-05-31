package com.example.feature.detection

import android.graphics.Bitmap
import com.example.core.service.FakeObjectDetectionService
import com.example.domain.model.DetectedObject
import com.example.core.service.FakeAnalyticsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class ObjectDetectionViewModelTest {

    private lateinit var viewModel: ObjectDetectionViewModel
    private val detectionService = FakeObjectDetectionService()
    private val analyticsService = FakeAnalyticsService()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ObjectDetectionViewModel(detectionService, analyticsService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `detectObjects should update UI state to Success when objects are found`() = runTest {
        val mockBitmap = mock<Bitmap>()
        detectionService.mockResults = listOf(DetectedObject("cat", 0.9f))

        viewModel.detectObjects(mockBitmap)

        assertTrue(viewModel.uiState.value is DetectionUiState.Success)
        val successState = viewModel.uiState.value as DetectionUiState.Success
        assertEquals(1, successState.objects.size)
        assertEquals("cat", successState.objects[0].label)
    }

    @Test
    fun `detectObjects should update UI state to Empty when no objects are found`() = runTest {
        val mockBitmap = mock<Bitmap>()
        detectionService.mockResults = emptyList()

        viewModel.detectObjects(mockBitmap)

        assertTrue(viewModel.uiState.value is DetectionUiState.Empty)
    }
}
