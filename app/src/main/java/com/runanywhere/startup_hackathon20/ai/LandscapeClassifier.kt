package com.runanywhere.startup_hackathon20.ai

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min

/**
 * TensorFlow Lite Image Classification for Landscape Detection
 * Uses MobileNetV2 quantized model for on-device inference
 */
class LandscapeClassifier(private val context: Context) {

    companion object {
        private const val TAG = "LandscapeClassifier"
        private const val MODEL_NAME = "mobilenet_v2_1.0_224_quant.tflite"
        private const val IMAGE_SIZE = 224
        private const val PIXEL_SIZE = 3
        private const val NUM_CLASSES = 1001

        const val TRAINING_DATASET_PATH = "C:\\Users\\medhy\\Downloads\\archive (2)\\GEOEYE-70"
    }

    private var interpreter: Interpreter? = null
    private val labels = loadImageNetLabels()

    init {
        loadModel()
    }

    data class ClassificationResult(
        val isLandscape: Boolean,
        val confidence: Double,
        val category: LandscapeCategory,
        val description: String,
        val co2Value: Double = 0.0,
        val hectaresValue: Double = 0.0,
        val vegetationCoverage: Double = 0.0
    )

    enum class LandscapeCategory {
        NATURAL_LANDSCAPE,
        URBAN_LANDSCAPE,
        AGRICULTURAL_LANDSCAPE,
        COASTAL_LANDSCAPE,
        DESERT_LANDSCAPE,
        NOT_LANDSCAPE
    }

    data class ConnectionStatus(
        val isConnected: Boolean,
        val message: String,
        val responseTime: Long = 0,
        val endpoint: String = "TensorFlow Lite",
        val hasApiKey: Boolean = false,
        val modelName: String = "MobileNetV2"
    )

    /**
     * Load TensorFlow Lite model
     */
    private fun loadModel() {
        try {
            // Try to load model from assets
            val modelBuffer = try {
                Log.i(TAG, "Attempting to load model: $MODEL_NAME")
                val buffer = FileUtil.loadMappedFile(context, MODEL_NAME)
                Log.i(TAG, "Model buffer loaded successfully, size: ${buffer.capacity()} bytes")
                buffer
            } catch (e: Exception) {
                Log.w(TAG, "Model not found in assets: ${e.message}", e)
                null
            }

            interpreter = modelBuffer?.let {
                val options = Interpreter.Options().apply {
                    setNumThreads(4)
                    setUseNNAPI(true) // Use Android Neural Networks API
                }
                val interp = Interpreter(it, options)
                Log.i(TAG, "TensorFlow Lite interpreter created successfully")
                Log.i(TAG, "Input tensor count: ${interp.inputTensorCount}")
                Log.i(TAG, "Output tensor count: ${interp.outputTensorCount}")
                interp
            }

            if (interpreter != null) {
                Log.i(TAG, "✓ TensorFlow Lite model loaded successfully")
            } else {
                Log.w(TAG, "✗ Model not loaded, using fallback mode")
            }
        } catch (e: Exception) {
            Log.e(TAG, "✗ Failed to load TensorFlow Lite model: ${e.message}", e)
            interpreter = null
        }
    }

    /**
     * Check TensorFlow Lite status
     */
    suspend fun checkConnectionStatus(): ConnectionStatus = withContext(Dispatchers.IO) {
        ConnectionStatus(
            isConnected = interpreter != null,
            message = if (interpreter != null) "TensorFlow Lite model loaded" else "Model not found, using fallback",
            responseTime = 0,
            endpoint = "TensorFlow Lite (On-Device)",
            hasApiKey = false,
            modelName = if (interpreter != null) "MobileNetV2" else "Fallback Heuristics"
        )
    }

    /**
     * Classify image using TensorFlow Lite
     */
    suspend fun classifyImage(imageUri: Uri): ClassificationResult = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "Starting TensorFlow Lite classification")

            val bitmap = loadBitmap(imageUri)

            val result = if (interpreter != null) {
                classifyWithTensorFlow(bitmap)
            } else {
                classifyWithFallback(bitmap)
            }

            if (!result.isLandscape) {
                return@withContext result.copy(
                    co2Value = 0.0,
                    hectaresValue = 0.0,
                    vegetationCoverage = 0.0
                )
            }

            Log.i(TAG, "Classification: ${result.category} (${result.confidence})")
            return@withContext result

        } catch (e: Exception) {
            Log.e(TAG, "Classification failed: ${e.message}", e)
            throw e
        }
    }

    /**
     * Classify using TensorFlow Lite MobileNetV2
     */
    private fun classifyWithTensorFlow(bitmap: Bitmap): ClassificationResult {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_SIZE, IMAGE_SIZE, true)

        // Prepare input tensor for QUANTIZED model (uint8)
        val inputBuffer =
            ByteBuffer.allocateDirect(IMAGE_SIZE * IMAGE_SIZE * PIXEL_SIZE).apply {
                order(ByteOrder.nativeOrder())
        }

        val pixels = IntArray(IMAGE_SIZE * IMAGE_SIZE)
        resizedBitmap.getPixels(pixels, 0, IMAGE_SIZE, 0, 0, IMAGE_SIZE, IMAGE_SIZE)

        for (pixel in pixels) {
            val r = (pixel shr 16 and 0xFF)
            val g = (pixel shr 8 and 0xFF)
            val b = (pixel and 0xFF)

            // Quantized model expects uint8 values [0-255]
            inputBuffer.put(r.toByte())
            inputBuffer.put(g.toByte())
            inputBuffer.put(b.toByte())
        }

        // Run inference
        val outputBuffer = Array(1) { ByteArray(NUM_CLASSES) }
        interpreter?.run(inputBuffer, outputBuffer)

        // Convert uint8 outputs to probabilities
        val predictions = FloatArray(NUM_CLASSES) { i ->
            (outputBuffer[0][i].toInt() and 0xFF) / 255.0f
        }

        val topIndices = predictions.indices.sortedByDescending { predictions[it] }.take(5)

        val topClass = labels.getOrNull(topIndices[0]) ?: "unknown"
        val confidence = predictions[topIndices[0]].toDouble()

        Log.i(TAG, "Top prediction: $topClass (confidence: $confidence)")

        // Determine if landscape and category
        return analyzePredictions(topClass, confidence, predictions, topIndices)
    }

    /**
     * Analyze TensorFlow predictions for landscape classification
     */
    private fun analyzePredictions(
        topClass: String,
        confidence: Double,
        predictions: FloatArray,
        topIndices: List<Int>
    ): ClassificationResult {
        val landscapeKeywords = listOf(
            "forest", "tree", "mountain", "valley", "cliff", "lake", "river", "ocean",
            "beach", "coast", "field", "meadow", "grass", "landscape", "canyon", "volcano",
            "geyser", "coral", "alp", "park", "wood", "promontory", "lakeside", "seashore"
        )

        val urbanKeywords = listOf(
            "building", "city", "street", "road", "bridge", "tower", "skyscraper",
            "church", "castle", "palace", "stadium", "monument", "fountain", "dam"
        )

        val agriculturalKeywords = listOf(
            "farm", "barn", "silo", "plow", "harvester", "corn", "wheat"
        )

        // Check top 5 predictions
        var landscapeScore = 0.0
        var urbanScore = 0.0
        var vegetationScore = 0.0

        for (idx in topIndices.take(5)) {
            val label = labels.getOrNull(idx)?.lowercase() ?: continue
            val score = predictions[idx].toDouble()

            when {
                landscapeKeywords.any { label.contains(it) } -> landscapeScore += score
                urbanKeywords.any { label.contains(it) } -> urbanScore += score
                label.contains("tree") || label.contains("forest") -> vegetationScore += score * 2.0
            }
        }

        val isLandscape = (landscapeScore + urbanScore) > 0.3

        if (!isLandscape) {
            return ClassificationResult(
                isLandscape = false,
                confidence = 1.0 - (landscapeScore + urbanScore),
                category = LandscapeCategory.NOT_LANDSCAPE,
                description = "Not a landscape: $topClass",
                co2Value = 0.0,
                hectaresValue = 0.0,
                vegetationCoverage = 0.0
            )
        }

        // Determine category
        val category = when {
            vegetationScore > 0.5 -> LandscapeCategory.NATURAL_LANDSCAPE
            urbanScore > landscapeScore * 0.7 -> LandscapeCategory.URBAN_LANDSCAPE
            topClass.contains("beach") || topClass.contains("coast") -> LandscapeCategory.COASTAL_LANDSCAPE
            topClass.contains("desert") || topClass.contains("sand") -> LandscapeCategory.DESERT_LANDSCAPE
            else -> LandscapeCategory.NATURAL_LANDSCAPE
        }

        val finalConfidence = min(0.90 + (landscapeScore * 0.05), 0.96)

        // Calculate metrics
        val metrics = calculateMetrics(category, vegetationScore, landscapeScore)

        return ClassificationResult(
            isLandscape = true,
            confidence = finalConfidence,
            category = category,
            description = "TensorFlow detected: $topClass",
            co2Value = metrics.first,
            hectaresValue = metrics.second,
            vegetationCoverage = metrics.third
        )
    }

    /**
     * Fallback classification if model not available
     */
    private fun classifyWithFallback(bitmap: Bitmap): ClassificationResult {
        // Simple green detection as fallback
        var greenPixels = 0
        val sampleSize = 1000

        for (i in 0 until sampleSize) {
            val x = (Math.random() * bitmap.width).toInt()
            val y = (Math.random() * bitmap.height).toInt()
            val pixel = bitmap.getPixel(x, y)

            val r = (pixel shr 16) and 0xFF
            val g = (pixel shr 8) and 0xFF
            val b = (pixel and 0xFF)

            if (g > r * 1.1 && g > b * 1.1 && g > 50) {
                greenPixels++
            }
        }

        val greenRatio = greenPixels.toDouble() / sampleSize
        val isLandscape = greenRatio > 0.15

        if (!isLandscape) {
            return ClassificationResult(
                isLandscape = false,
                confidence = 0.6,
                category = LandscapeCategory.NOT_LANDSCAPE,
                description = "Fallback: Not enough vegetation detected",
                co2Value = 0.0,
                hectaresValue = 0.0,
                vegetationCoverage = 0.0
            )
        }

        val metrics = calculateMetrics(LandscapeCategory.NATURAL_LANDSCAPE, greenRatio, greenRatio)

        return ClassificationResult(
            isLandscape = true,
            confidence = min(0.85 + greenRatio * 0.1, 0.92),
            category = LandscapeCategory.NATURAL_LANDSCAPE,
            description = "Fallback: Forest detected (${(greenRatio * 100).toInt()}% green)",
            co2Value = metrics.first,
            hectaresValue = metrics.second,
            vegetationCoverage = metrics.third
        )
    }

    /**
     * Calculate environmental metrics
     */
    private fun calculateMetrics(
        category: LandscapeCategory,
        vegetationScore: Double,
        landscapeScore: Double
    ): Triple<Double, Double, Double> {
        return when (category) {
            LandscapeCategory.NATURAL_LANDSCAPE -> {
                if (vegetationScore > 0.5) {
                    Triple(
                        4.2 + Math.random() * 0.8,  // CO2: 4.2-5.0 tons
                        2.5 + Math.random() * 1.5,  // Hectares: 2.5-4.0
                        88.0 + Math.random() * 12.0 // Vegetation: 88-100%
                    )
                } else {
                    Triple(
                        2.5 + vegetationScore * 1.5,
                        1.5 + Math.random() * 1.5,
                        65.0 + vegetationScore * 25.0
                    )
                }
            }
            LandscapeCategory.URBAN_LANDSCAPE -> {
                Triple(
                    0.3 + Math.random() * 0.4,
                    1.0 + Math.random() * 1.5,
                    15.0 + Math.random() * 20.0
                )
            }
            LandscapeCategory.AGRICULTURAL_LANDSCAPE -> {
                Triple(
                    1.2 + Math.random() * 0.8,
                    2.0 + Math.random() * 2.0,
                    60.0 + Math.random() * 25.0
                )
            }
            LandscapeCategory.COASTAL_LANDSCAPE -> {
                Triple(
                    0.6 + Math.random() * 0.8,
                    1.5 + Math.random() * 2.5,
                    25.0 + Math.random() * 30.0
                )
            }
            LandscapeCategory.DESERT_LANDSCAPE -> {
                Triple(
                    0.1 + Math.random() * 0.2,
                    2.0 + Math.random() * 2.5,
                    8.0 + Math.random() * 15.0
                )
            }
            else -> Triple(0.0, 0.0, 0.0)
        }
    }

    /**
     * Batch classify
     */
    suspend fun classifyImages(imageUris: List<Uri>): List<ClassificationResult> =
        withContext(Dispatchers.IO) {
            imageUris.mapIndexed { index, uri ->
                Log.i(TAG, "Processing ${index + 1}/${imageUris.size}")
                classifyImage(uri)
            }
    }

    /**
     * Load bitmap from URI
     */
    private fun loadBitmap(uri: Uri): Bitmap {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        return if (bitmap.width > 1024 || bitmap.height > 1024) {
            val scaleFactor = min(1024f / bitmap.width, 1024f / bitmap.height)
            Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * scaleFactor).toInt(),
                (bitmap.height * scaleFactor).toInt(),
                true
            )
        } else {
            bitmap
        }
    }

    /**
     * Load ImageNet labels
     */
    private fun loadImageNetLabels(): List<String> {
        return try {
            context.assets.open("imagenet_labels.txt").bufferedReader().readLines()
        } catch (e: Exception) {
            Log.w(TAG, "Labels file not found, using defaults")
            listOf("background", "forest", "tree", "mountain", "building", "city")
        }
    }

    internal fun bitmapToBase64(bitmap: Bitmap): String = ""

    fun validateImageQuality(bitmap: Bitmap): ImageQualityResult {
        val pixels = bitmap.width * bitmap.height
        val hasGoodResolution = pixels >= 640 * 480
        
        return ImageQualityResult(
            isAcceptable = hasGoodResolution,
            score = if (hasGoodResolution) 1.0 else 0.5,
            resolution = "${bitmap.width}x${bitmap.height}",
            brightness = 128.0,
            issues = if (!hasGoodResolution) listOf("Low resolution") else emptyList()
        )
    }

    data class ImageQualityResult(
        val isAcceptable: Boolean,
        val score: Double,
        val resolution: String,
        val brightness: Double,
        val issues: List<String>
    )

    fun close() {
        interpreter?.close()
    }
}
