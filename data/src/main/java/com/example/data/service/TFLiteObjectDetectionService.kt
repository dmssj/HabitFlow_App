package com.example.data.service

import android.content.Context
import android.graphics.Bitmap
import com.example.domain.model.BoundingBox
import com.example.domain.model.DetectedObject
import com.example.domain.service.ObjectDetectionService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TFLiteObjectDetectionService @Inject constructor(
    @ApplicationContext private val context: Context
) : ObjectDetectionService {

    private var interpreter: Interpreter? = null
    private var labels: List<String> = emptyList()

    private fun initInterpreter() {
        if (interpreter != null) return
        try {
            val modelBuffer = FileUtil.loadMappedFile(context, "model.tflite")
            val options = Interpreter.Options()
            interpreter = Interpreter(modelBuffer, options)
            labels = FileUtil.loadLabels(context, "labels.txt")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun detectObjects(bitmap: Bitmap): List<DetectedObject> = withContext(Dispatchers.Default) {
        initInterpreter()
        val tflite = interpreter ?: return@withContext emptyList()

        // 1. Preprocessing
        // SSD Mobilenet usually expects 300x300 or similar
        // EfficientDet expects different sizes. 
        // For this lab, let's assume a standard 300x300 model or get from input tensor shape
        val inputShape = tflite.getInputTensor(0).shape() // {1, height, width, 3}
        val targetHeight = inputShape[1]
        val targetWidth = inputShape[2]

        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(targetHeight, targetWidth, ResizeOp.ResizeMethod.BILINEAR))
            .build()

        val tensorImage = TensorImage(tflite.getInputTensor(0).dataType())
        tensorImage.load(bitmap)
        val processedImage = imageProcessor.process(tensorImage)

        // 2. Prepare Outputs
        // SSD Mobilenet V1 output tensors:
        // 0: Locations [1, 10, 4]
        // 1: Classes [1, 10]
        // 2: Scores [1, 10]
        // 3: Number of detections [1]
        
        val locations = Array(1) { Array(10) { FloatArray(4) } }
        val classes = Array(1) { FloatArray(10) }
        val scores = Array(1) { FloatArray(10) }
        val numDetections = FloatArray(1)

        val outputMap = mutableMapOf<Int, Any>()
        outputMap[0] = locations
        outputMap[1] = classes
        outputMap[2] = scores
        outputMap[3] = numDetections

        // 3. Inference
        tflite.runForMultipleInputsOutputs(arrayOf(processedImage.buffer), outputMap)

        // 4. Postprocessing
        val results = mutableListOf<DetectedObject>()
        for (i in 0 until numDetections[0].toInt()) {
            val score = scores[0][i]
            if (score > 0.5f) { // Threshold
                val classIndex = classes[0][i].toInt()
                val label = labels.getOrNull(classIndex) ?: "Unknown"
                
                // SSD output: [ymin, xmin, ymax, xmax]
                val rect = locations[0][i]
                val boundingBox = BoundingBox(
                    left = rect[1],
                    top = rect[0],
                    right = rect[3],
                    bottom = rect[2]
                )
                
                results.add(DetectedObject(label, score, boundingBox))
            }
        }

        results.sortByDescending { it.confidence }
        results
    }
    
    fun close() {
        interpreter?.close()
        interpreter = null
    }
}
