package com.runanywhere.startup_hackathon20.ai

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Training script for landscape detection model
 *
 * Usage:
 * 1. Place GEOEYE-70 dataset in: C:\Users\medhy\Downloads\archive (2)
 * 2. Call trainModelWithDataset() from your application
 * 3. Training files will be generated in: /sdcard/Download/landscape_training/
 * 4. Use these files to train LocalAI model
 */
object TrainingScript {

    private const val TAG = "TrainingScript"

    // Dataset paths
    private const val DATASET_PATH = "C:\\Users\\medhy\\Downloads\\archive (2)"

    /**
     * Main training function - call this to start training
     */
    suspend fun trainModelWithDataset(
        context: Context,
        datasetPath: String = DATASET_PATH,
        outputPath: String? = null
    ): Result<ModelTrainer.TrainingReport> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Starting training process...")

                // Determine output path
                val output = outputPath ?: "${context.getExternalFilesDir(null)}/landscape_training"

                Log.d(TAG, "Dataset: $datasetPath")
                Log.d(TAG, "Output: $output")

                // Create trainer
                val trainer = ModelTrainer(context)

                // Train model
                val result = trainer.trainModel(
                    datasetPath = datasetPath,
                    outputPath = output,
                    onProgress = { progress, message ->
                        Log.d(TAG, "Progress: $progress% - $message")
                    }
                )

                result.onSuccess { report ->
                    Log.d(TAG, "Training completed successfully!")
                    Log.d(TAG, report.toString())
                    Log.d(TAG, "\nNext steps:")
                    Log.d(TAG, "1. Copy training files from: $output")
                    Log.d(TAG, "2. Set up LocalAI server")
                    Log.d(TAG, "3. Upload training files to LocalAI")
                    Log.d(TAG, "4. Start fine-tuning job")
                    Log.d(TAG, "5. Test the model")
                }

                result.onFailure { error ->
                    Log.e(TAG, "Training failed: ${error.message}", error)
                }

                result
            } catch (e: Exception) {
                Log.e(TAG, "Training error", e)
                Result.failure(e)
            }
        }
    }

    /**
     * Quick test function to verify dataset is accessible
     */
    suspend fun verifyDataset(datasetPath: String = DATASET_PATH): Result<DatasetInfo> {
        return withContext(Dispatchers.IO) {
            try {
                val datasetDir = File(datasetPath)

                if (!datasetDir.exists()) {
                    return@withContext Result.failure(Exception("Dataset directory not found: $datasetPath"))
                }

                if (!datasetDir.isDirectory) {
                    return@withContext Result.failure(Exception("Path is not a directory: $datasetPath"))
                }

                // Count images
                var imageCount = 0
                val extensions = setOf("jpg", "jpeg", "png", "tif", "tiff")

                datasetDir.walkTopDown()
                    .filter { it.isFile && it.extension.lowercase() in extensions }
                    .forEach { imageCount++ }

                val info = DatasetInfo(
                    path = datasetPath,
                    exists = true,
                    totalImages = imageCount,
                    sizeBytes = calculateDirectorySize(datasetDir)
                )

                Log.d(TAG, "Dataset verification:")
                Log.d(TAG, "  Path: ${info.path}")
                Log.d(TAG, "  Total images: ${info.totalImages}")
                Log.d(TAG, "  Size: ${info.sizeBytes / 1024 / 1024} MB")

                Result.success(info)
            } catch (e: Exception) {
                Log.e(TAG, "Dataset verification failed", e)
                Result.failure(e)
            }
        }
    }

    private fun calculateDirectorySize(directory: File): Long {
        var size = 0L
        directory.walkTopDown().forEach { file ->
            if (file.isFile) {
                size += file.length()
            }
        }
        return size
    }

    data class DatasetInfo(
        val path: String,
        val exists: Boolean,
        val totalImages: Int,
        val sizeBytes: Long
    )
}

/**
 * Helper function to run training in a coroutine scope
 */
fun Context.startModelTraining(
    datasetPath: String = "C:\\Users\\medhy\\Downloads\\archive (2)",
    outputPath: String? = null,
    onComplete: (Result<ModelTrainer.TrainingReport>) -> Unit = {}
) {
    CoroutineScope(Dispatchers.Main).launch {
        val result = TrainingScript.trainModelWithDataset(
            context = this@startModelTraining,
            datasetPath = datasetPath,
            outputPath = outputPath
        )
        onComplete(result)
    }
}

/**
 * Helper function to verify dataset
 */
fun Context.verifyDatasetAccess(
    datasetPath: String = "C:\\Users\\medhy\\Downloads\\archive (2)",
    onComplete: (Result<TrainingScript.DatasetInfo>) -> Unit = {}
) {
    CoroutineScope(Dispatchers.Main).launch {
        val result = TrainingScript.verifyDataset(datasetPath)
        onComplete(result)
    }
}
