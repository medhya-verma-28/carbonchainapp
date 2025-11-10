package com.runanywhere.startup_hackathon20.ai

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Utility class for training the landscape detection model
 */
class ModelTrainer(private val context: Context) {

    companion object {
        private const val TAG = "ModelTrainer"
    }

    /**
     * Complete training pipeline:
     * 1. Clean and prepare dataset
     * 2. Create training/validation splits
     * 3. Generate training files for LocalAI
     */
    suspend fun trainModel(
        datasetPath: String,
        outputPath: String,
        onProgress: (Int, String) -> Unit
    ): Result<TrainingReport> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting model training pipeline...")
            onProgress(0, "Initializing training...")

            val datasetDir = File(datasetPath)
            if (!datasetDir.exists() || !datasetDir.isDirectory) {
                return@withContext Result.failure(Exception("Dataset directory not found: $datasetPath"))
            }

            // Step 1: Clean dataset
            onProgress(10, "Cleaning dataset...")
            val cleanResult = cleanDataset(datasetDir)
            Log.d(
                TAG,
                "Cleaned dataset: ${cleanResult.validImages} valid images, ${cleanResult.removedImages} removed"
            )

            // Step 2: Prepare output directory
            val outputDir = File(outputPath)
            if (!outputDir.exists()) {
                outputDir.mkdirs()
            }

            // Step 3: Split dataset (80% train, 10% validation, 10% test)
            onProgress(20, "Splitting dataset...")
            val split = splitDataset(cleanResult.validImageFiles, 0.8, 0.1)
            Log.d(
                TAG,
                "Dataset split - Train: ${split.train.size}, Val: ${split.validation.size}, Test: ${split.test.size}"
            )

            // Step 4: Generate training data
            onProgress(30, "Generating training data...")
            val trainResult = generateTrainingFile(
                split.train,
                File(outputDir, "train.jsonl"),
                onProgress = { progress, msg ->
                    onProgress(30 + (progress * 0.3).toInt(), msg)
                }
            )

            // Step 5: Generate validation data
            onProgress(60, "Generating validation data...")
            val valResult = generateTrainingFile(
                split.validation,
                File(outputDir, "validation.jsonl"),
                onProgress = { progress, msg ->
                    onProgress(60 + (progress * 0.15).toInt(), msg)
                }
            )

            // Step 6: Generate test data
            onProgress(75, "Generating test data...")
            val testResult = generateTrainingFile(
                split.test,
                File(outputDir, "test.jsonl"),
                onProgress = { progress, msg ->
                    onProgress(75 + (progress * 0.15).toInt(), msg)
                }
            )

            // Step 7: Create model configuration
            onProgress(90, "Creating model configuration...")
            createModelConfig(outputDir, trainResult, valResult)

            // Step 8: Generate training instructions
            onProgress(95, "Generating training instructions...")
            generateTrainingInstructions(outputDir)

            onProgress(100, "Training preparation complete!")

            val report = TrainingReport(
                totalImages = cleanResult.validImages,
                trainImages = trainResult,
                validationImages = valResult,
                testImages = testResult,
                outputPath = outputPath,
                trainingFileSize = File(outputDir, "train.jsonl").length(),
                validationFileSize = File(outputDir, "validation.jsonl").length(),
                testFileSize = File(outputDir, "test.jsonl").length()
            )

            Result.success(report)

        } catch (e: Exception) {
            Log.e(TAG, "Training failed", e)
            Result.failure(e)
        }
    }

    /**
     * Clean dataset by removing corrupted or invalid images
     */
    private suspend fun cleanDataset(datasetDir: File): CleanResult = withContext(Dispatchers.IO) {
        val validImages = mutableListOf<File>()
        var removedCount = 0

        datasetDir.walkTopDown()
            .filter {
                it.isFile && it.extension.lowercase() in listOf(
                    "jpg",
                    "jpeg",
                    "png",
                    "tif",
                    "tiff"
                )
            }
            .forEach { imageFile ->
                try {
                    // Try to decode the image to check if it's valid
                    val options = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }
                    BitmapFactory.decodeFile(imageFile.absolutePath, options)

                    // Check if image has valid dimensions
                    if (options.outWidth > 0 && options.outHeight > 0) {
                        // Check minimum size (at least 224x224 for decent training)
                        if (options.outWidth >= 224 && options.outHeight >= 224) {
                            validImages.add(imageFile)
                        } else {
                            Log.d(
                                TAG,
                                "Removed small image: ${imageFile.name} (${options.outWidth}x${options.outHeight})"
                            )
                            removedCount++
                        }
                    } else {
                        Log.d(TAG, "Removed corrupted image: ${imageFile.name}")
                        removedCount++
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "Removed invalid image: ${imageFile.name} - ${e.message}")
                    removedCount++
                }
            }

        CleanResult(validImages.size, removedCount, validImages)
    }

    /**
     * Split dataset into train/validation/test sets
     */
    private fun splitDataset(
        images: List<File>,
        trainRatio: Double,
        valRatio: Double
    ): DatasetSplit {
        val shuffled = images.shuffled()
        val trainSize = (shuffled.size * trainRatio).toInt()
        val valSize = (shuffled.size * valRatio).toInt()

        return DatasetSplit(
            train = shuffled.subList(0, trainSize),
            validation = shuffled.subList(trainSize, trainSize + valSize),
            test = shuffled.subList(trainSize + valSize, shuffled.size)
        )
    }

    /**
     * Generate JSONL training file for LocalAI
     */
    private suspend fun generateTrainingFile(
        images: List<File>,
        outputFile: File,
        onProgress: (Int, String) -> Unit
    ): Int = withContext(Dispatchers.IO) {
        val writer = outputFile.bufferedWriter()
        var processedCount = 0

        images.forEachIndexed { index, imageFile ->
            try {
                // Load image
                val options = BitmapFactory.Options().apply {
                    inSampleSize = 2 // Downsample by 2x for faster training
                }
                val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath, options)

                if (bitmap != null) {
                    // Convert to base64
                    val classifier = LandscapeClassifier(context)
                    val base64Image = classifier.bitmapToBase64(bitmap)

                    // Determine label (all GEOEYE images are landscapes)
                    val category = determineLandscapeCategory(imageFile.path)
                    val isLandscape =
                        category != LandscapeClassifier.LandscapeCategory.NOT_LANDSCAPE

                    // Create training example in chat format
                    val trainingExample = JSONObject().apply {
                        put("messages", JSONArray().apply {
                            // User message with image
                            put(JSONObject().apply {
                                put("role", "user")
                                put("content", JSONArray().apply {
                                    put(JSONObject().apply {
                                        put("type", "text")
                                        put(
                                            "text",
                                            "Analyze this image and determine if it shows an earth landscape (natural or urban geographical area). Respond in JSON format with isLandscape (boolean), confidence (0.0-1.0), category, and description."
                                        )
                                    })
                                    put(JSONObject().apply {
                                        put("type", "image_url")
                                        put("image_url", JSONObject().apply {
                                            put("url", "data:image/jpeg;base64,$base64Image")
                                        })
                                    })
                                })
                            })
                            // Assistant response
                            put(JSONObject().apply {
                                put("role", "assistant")
                                put("content", JSONObject().apply {
                                    put("isLandscape", isLandscape)
                                    put("confidence", 0.95)
                                    put("category", category.name)
                                    put(
                                        "description",
                                        generateDescription(category, imageFile.name)
                                    )
                                }.toString())
                            })
                        })
                    }

                    writer.write(trainingExample.toString())
                    writer.newLine()
                    processedCount++

                    bitmap.recycle()

                    // Update progress
                    if (processedCount % 50 == 0) {
                        val progress = ((index + 1) * 100) / images.size
                        onProgress(progress, "Processed $processedCount/${images.size} images...")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing ${imageFile.name}: ${e.message}")
            }
        }

        writer.close()
        processedCount
    }

    /**
     * Determine landscape category from file path
     */
    private fun determineLandscapeCategory(path: String): LandscapeClassifier.LandscapeCategory {
        val lowerPath = path.lowercase()
        return when {
            lowerPath.contains("forest") || lowerPath.contains("tree") || lowerPath.contains("wood") ->
                LandscapeClassifier.LandscapeCategory.NATURAL_LANDSCAPE

            lowerPath.contains("urban") || lowerPath.contains("city") || lowerPath.contains("building") ->
                LandscapeClassifier.LandscapeCategory.URBAN_LANDSCAPE

            lowerPath.contains("farm") || lowerPath.contains("agriculture") || lowerPath.contains("crop") || lowerPath.contains(
                "field"
            ) ->
                LandscapeClassifier.LandscapeCategory.AGRICULTURAL_LANDSCAPE

            lowerPath.contains("coast") || lowerPath.contains("beach") || lowerPath.contains("ocean") || lowerPath.contains(
                "sea"
            ) ->
                LandscapeClassifier.LandscapeCategory.COASTAL_LANDSCAPE

            lowerPath.contains("desert") || lowerPath.contains("arid") || lowerPath.contains("sand") ->
                LandscapeClassifier.LandscapeCategory.DESERT_LANDSCAPE

            else -> LandscapeClassifier.LandscapeCategory.NATURAL_LANDSCAPE // Default
        }
    }

    /**
     * Generate description for training example
     */
    private fun generateDescription(
        category: LandscapeClassifier.LandscapeCategory,
        filename: String
    ): String {
        return when (category) {
            LandscapeClassifier.LandscapeCategory.NATURAL_LANDSCAPE ->
                "Natural landscape showing geographical terrain with natural features"

            LandscapeClassifier.LandscapeCategory.URBAN_LANDSCAPE ->
                "Urban landscape with buildings and city infrastructure"

            LandscapeClassifier.LandscapeCategory.AGRICULTURAL_LANDSCAPE ->
                "Agricultural landscape with farmland and cultivated areas"

            LandscapeClassifier.LandscapeCategory.COASTAL_LANDSCAPE ->
                "Coastal landscape showing beach or ocean areas"

            LandscapeClassifier.LandscapeCategory.DESERT_LANDSCAPE ->
                "Desert landscape with arid terrain"

            LandscapeClassifier.LandscapeCategory.NOT_LANDSCAPE ->
                "Image does not show geographical landscape"
        }
    }

    /**
     * Create model configuration file
     */
    private fun createModelConfig(outputDir: File, trainCount: Int, valCount: Int) {
        val configFile = File(outputDir, "model_config.json")
        val config = JSONObject().apply {
            put("model_name", "landscape-detector")
            put("base_model", "gpt-4-vision-preview")
            put("task", "image-classification")
            put("training_file", "train.jsonl")
            put("validation_file", "validation.jsonl")
            put("test_file", "test.jsonl")
            put("hyperparameters", JSONObject().apply {
                put("n_epochs", 3)
                put("batch_size", 4)
                put("learning_rate", 0.00005)
                put("warmup_steps", 100)
                put("weight_decay", 0.01)
            })
            put("training_samples", trainCount)
            put("validation_samples", valCount)
            put("categories", JSONArray().apply {
                LandscapeClassifier.LandscapeCategory.values().forEach { put(it) }
            })
            put(
                "description",
                "Model trained to detect earth landscapes (natural and urban) in images for carbon registry verification"
            )
        }
        configFile.writeText(config.toString(2))
    }

    /**
     * Generate training instructions README
     */
    private fun generateTrainingInstructions(outputDir: File) {
        val readmeFile = File(outputDir, "TRAINING_INSTRUCTIONS.md")
        val instructions = """
# Landscape Detection Model Training

## Overview
This model is trained to detect earth landscapes (natural and urban geographical areas) in images for carbon registry project verification.

## Dataset Information
- **Source**: GEOEYE-70 satellite imagery dataset
- **Purpose**: Carbon credit verification through landscape detection
- **Categories**:
  - Natural Landscape (forests, mountains, rivers, etc.)
  - Urban Landscape (cities, buildings, infrastructure)
  - Agricultural Landscape (farms, fields, plantations)
  - Coastal Landscape (beaches, coastal areas)
  - Desert Landscape (desert, arid regions)
  - Not Landscape (non-geographical images)

## Training with LocalAI

### 1. Install LocalAI
```bash
# Using Docker
docker run -p 8080:8080 --name local-ai -ti localai/localai:latest
```

### 2. Upload Training Data
Place the generated JSONL files in LocalAI's data directory:
- `train.jsonl` - Training data
- `validation.jsonl` - Validation data
- `test.jsonl` - Test data

### 3. Start Fine-tuning
```bash
curl http://localhost:8080/v1/fine-tuning/jobs \
  -H "Content-Type: application/json" \
  -d '{
    "training_file": "train.jsonl",
    "validation_file": "validation.jsonl",
    "model": "gpt-4-vision-preview",
    "hyperparameters": {
      "n_epochs": 3,
      "batch_size": 4,
      "learning_rate": 0.00005
    }
  }'
```

### 4. Monitor Training
```bash
curl http://localhost:8080/v1/fine-tuning/jobs/<job_id>
```

### 5. Deploy Model
Once training completes, the model `landscape-detector` will be available at:
```
http://localhost:8080/v1/chat/completions
```

## Model Usage in App

The model is integrated into the app through the `LandscapeClassifier` class:

1. **Image Upload**: When users upload photos for carbon registry projects
2. **AI Classification**: Images are sent to LocalAI for landscape detection
3. **Verification**: Results determine verification checklist markers
4. **Admin Review**: Admins see AI confidence scores in verification portal

## Classification Criteria

### POSITIVE Results (Landscape Detected)
- Natural environments (forests, mountains, rivers, oceans, deserts, fields)
- Urban landscapes (cities, buildings, streets, infrastructure)
- Agricultural areas (farms, plantations, cultivated land)
- Coastal regions (beaches, shorelines)
- Any geographical area of Earth

**Effect**: High AI confidence → More verification markers ticked off

### NEGATIVE Results (Not Landscape)
- Indoor scenes
- Close-up objects
- People portraits
- Animals (unless in landscape context)
- Food, products, abstract art
- Anything not showing geographical area

**Effect**: Low AI confidence → Fewer verification markers ticked off

## Model Performance Expectations

- **Accuracy Target**: >90% on test set
- **Precision**: High precision on landscape detection (minimize false positives)
- **Recall**: High recall to catch all legitimate landscapes
- **Confidence**: Reliable confidence scores for admin decision-making

## Integration Points

1. **User Upload Flow**: 
   - Photo Documentation section in user login
   - Real-time AI analysis on image upload
   - Immediate feedback to user

2. **Admin Verification**:
   - AI confidence displayed in verification checklist
   - Automatic marker updates based on AI results
   - Admin can override AI decisions

3. **Blockchain Registration**:
   - Only approved submissions with positive AI results get registered
   - AI confidence score stored in blockchain metadata

## Maintenance

- **Retraining**: Retrain model quarterly with new verified submissions
- **Data Augmentation**: Add edge cases and failure examples
- **Performance Monitoring**: Track accuracy on production data
- **Feedback Loop**: Use admin corrections to improve model

## Files Generated

- `train.jsonl` - Training dataset (80%)
- `validation.jsonl` - Validation dataset (10%)
- `test.jsonl` - Test dataset (10%)
- `model_config.json` - Model configuration
- `TRAINING_INSTRUCTIONS.md` - This file

## Next Steps

1. Review the generated training files
2. Set up LocalAI server (if not already running)
3. Start fine-tuning job
4. Monitor training progress
5. Test model on validation set
6. Deploy to production
7. Monitor real-world performance

## Support

For issues or questions about the training pipeline, refer to:
- LocalAI documentation: https://localai.io/
- Model training logs in Android Studio
- LandscapeClassifier.kt implementation

---
Generated by ModelTrainer.kt
        """.trimIndent()

        readmeFile.writeText(instructions)
    }

    // Data classes
    data class CleanResult(
        val validImages: Int,
        val removedImages: Int,
        val validImageFiles: List<File>
    )

    data class DatasetSplit(
        val train: List<File>,
        val validation: List<File>,
        val test: List<File>
    )

    data class TrainingReport(
        val totalImages: Int,
        val trainImages: Int,
        val validationImages: Int,
        val testImages: Int,
        val outputPath: String,
        val trainingFileSize: Long,
        val validationFileSize: Long,
        val testFileSize: Long
    ) {
        override fun toString(): String {
            return """
                Training Report
                ===============
                Total Images: $totalImages
                Train: $trainImages images
                Validation: $validationImages images
                Test: $testImages images
                
                Output Path: $outputPath
                Training File: ${trainingFileSize / 1024 / 1024} MB
                Validation File: ${validationFileSize / 1024 / 1024} MB
                Test File: ${testFileSize / 1024 / 1024} MB
            """.trimIndent()
        }
    }
}
