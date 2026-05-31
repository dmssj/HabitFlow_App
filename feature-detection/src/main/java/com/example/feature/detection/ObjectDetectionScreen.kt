package com.example.feature.detection

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun ObjectDetectionScreen(
    viewModel: ObjectDetectionViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    
    var showCamera by remember { mutableStateOf(false) }
    
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { viewModel.detectObjectsFromUri(context, it) }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            showCamera = true
            viewModel.onCameraOpened()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (showCamera) {
            CameraPreview(
                onPhotoTaken = { bitmap ->
                    showCamera = false
                    viewModel.detectObjects(bitmap)
                },
                onClose = { showCamera = false }
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    try {
                        galleryLauncher.launch(arrayOf("image/*"))
                    } catch (e: Exception) {
                        viewModel.onError("Не удалось открыть проводник", e)
                    }
                }) {
                    Text("Галерея")
                }
                Button(onClick = {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }) {
                    Text("Камера")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            when (val state = uiState) {
                is DetectionUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is DetectionUiState.Success -> {
                    Column {
                        Image(
                            bitmap = state.image.asImageBitmap(),
                            contentDescription = "Detected Image",
                            modifier = Modifier.fillMaxWidth().height(300.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Результаты распознавания:", style = MaterialTheme.typography.titleMedium)
                        LazyColumn {
                            items(state.objects) { obj ->
                                ListItem(
                                    headlineContent = { Text(obj.label) },
                                    trailingContent = { Text("${(obj.confidence * 100).toInt()}%") }
                                )
                            }
                        }
                    }
                }
                is DetectionUiState.Empty -> {
                    Text("Объекты не найдены")
                }
                is DetectionUiState.Error -> {
                    Text("Ошибка: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
                else -> {
                    Text("Выберите изображение для начала работы")
                }
            }
        }
    }
}

@Composable
fun CameraPreview(
    onPhotoTaken: (Bitmap) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val executor = remember { Executors.newSingleThreadExecutor() }

    LaunchedEffect(cameraProviderFuture) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
        
        Row(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = {
                val file = File(context.cacheDir, "temp_photo.jpg")
                val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
                imageCapture.takePicture(
                    outputOptions,
                    executor,
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                            onPhotoTaken(bitmap)
                        }
                        override fun onError(exception: ImageCaptureException) {
                            exception.printStackTrace()
                        }
                    }
                )
            }) {
                Text("Сделать фото")
            }
            Button(onClick = onClose) {
                Text("Закрыть")
            }
        }
    }
}
