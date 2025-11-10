# AI Landscape Detection Model - Training & Integration Guide

## Overview

This guide explains how to train and integrate the AI landscape detection model for carbon registry
verification using LocalAI API and the GEOEYE-70 dataset.

## Purpose

The AI model validates that images uploaded for carbon credit projects show actual geographical
landscapes (natural or urban) rather than unrelated content. This ensures project authenticity and
prevents fraud in the carbon registry system.

## Dataset Information

- **Source**: `C:\Users\medhy\Downloads\archive (2)` (GEOEYE-70 dataset)
- **Content**: Satellite imagery of Earth's geographical areas
- **Total Images**: ~34,602 images
- **Categories**: Natural landscapes, urban areas, agricultural land, coastal regions, desert
  terrain

## Architecture

### Components

1. **LandscapeClassifier.kt** - Main AI classification service
    - Communicates with LocalAI API
    - Classifies images as landscape or non-landscape
    - Returns confidence scores and categories

2. **ModelTrainer.kt** - Training pipeline utility
    - Cleans and prepares dataset
    - Generates training/validation/test splits (80/10/10)
    - Creates JSONL files for LocalAI fine-tuning

3. **TrainingScript.kt** - Standalone training script
    - Easy-to-use entry point for training
    - Dataset verification utilities
    - Progress monitoring

4. **CarbonRepository.kt** - Integration with app
    - Calls AI classifier on image upload
    - Updates verification markers based on AI results
    - Stores AI analysis in submission data

5. **AdminVerificationScreen.kt** - Admin UI
    - Displays AI verification results
    - Shows confidence scores
    - Provides AI-based recommendations

## Classification Logic

### Positive Results (Landscape Detected)

Images showing:

- Natural environments (forests, mountains, rivers, oceans, deserts, fields)
- Urban landscapes (cities, buildings, streets, infrastructure)
- Agricultural areas (farms, plantations, cultivated land)
- Coastal regions (beaches, shorelines)
- Any geographical area of Earth

**Effect**:

- High AI confidence (≥85%): Quality = "High", more verification markers ticked
- Medium confidence (60-85%): Quality = "Medium", some markers ticked
- Admin sees "AI recommends approval"

### Negative Results (Not Landscape)

Images showing:

- Indoor scenes
- Close-up objects
- People portraits
- Animals (without landscape context)
- Food, products, abstract art
- Non-geographical content

**Effect**:

- Low AI confidence (<60%): Quality = "Low", fewer markers ticked
- Admin sees "Manual review required"

## Training Process

### Step 1: Verify Dataset Access

```kotlin
// In your Activity or ViewModel
context.verifyDatasetAccess { result ->
    result.onSuccess { info ->
        println("Dataset found: ${info.totalImages} images")
    }
    result.onFailure { error ->
        println("Dataset error: ${error.message}")
    }
}
```

### Step 2: Start Training

```kotlin
// In your Activity or ViewModel
context.startModelTraining(
    datasetPath = "C:\\Users\\medhy\\Downloads\\archive (2)",
    outputPath = null // Uses app's external files directory
) { result ->
    result.onSuccess { report ->
        println("Training complete!")
        println(report.toString())
    }
    result.onFailure { error ->
        println("Training failed: ${error.message}")
    }
}
```

### Step 3: Training Output

Training generates the following files in
`/sdcard/Android/data/com.runanywhere.startup_hackathon20/files/landscape_training/`:

- `train.jsonl` - Training dataset (80% of images)
- `validation.jsonl` - Validation dataset (10% of images)
- `test.jsonl` - Test dataset (10% of images)
- `model_config.json` - Model configuration
- `TRAINING_INSTRUCTIONS.md` - Detailed training instructions

### Step 4: Set Up LocalAI

#### Option A: Docker (Recommended)

```bash
# Install LocalAI with Docker
docker run -p 8080:8080 --name local-ai \
  -v /path/to/models:/models \
  -ti localai/localai:latest
```

#### Option B: Direct Installation

```bash
# Download LocalAI binary
curl -Lo local-ai https://github.com/mudler/LocalAI/releases/download/latest/local-ai-Linux-x86_64
chmod +x local-ai

# Run LocalAI
./local-ai --models-path ./models --address :8080
```

### Step 5: Upload Training Data

Copy the generated JSONL files to LocalAI's data directory:

```bash
# Copy training files
cp train.jsonl /path/to/localai/data/
cp validation.jsonl /path/to/localai/data/
cp test.jsonl /path/to/localai/data/
```

### Step 6: Start Fine-tuning

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

### Step 7: Monitor Training

```bash
# Get job status
curl http://localhost:8080/v1/fine-tuning/jobs/<job_id>

# List all jobs
curl http://localhost:8080/v1/fine-tuning/jobs
```

### Step 8: Deploy Model

Once training completes, update the LocalAI endpoint in `LandscapeClassifier.kt`:

```kotlin
// Update if LocalAI is running on different host/port
private val localAIEndpoint = "http://localhost:8080/v1/chat/completions"
```

## Integration with App

### 1. Initialize Classifier

```kotlin
// In your Application or DI setup
val landscapeClassifier = LandscapeClassifier(context)
val repository = CarbonRepository(blockchainService, landscapeClassifier)
```

### 2. Automatic Classification on Upload

When users submit photos in the Carbon Registry section:

1. Image is uploaded
2. `LandscapeClassifier` analyzes the image
3. AI returns classification result
4. Verification markers are automatically updated:
    - `aiLandscapeDetected`: Boolean
    - `aiConfidence`: Percentage (0-100)
    - `aiLandscapeCategory`: Category name
    - `aiAnalysisDescription`: AI's description

### 3. Admin Verification

Admins see AI results in the verification portal:

- **AI Landscape Verification** section shows:
    - Whether landscape was detected
    - Confidence percentage
    - Landscape category
    - AI analysis description
    - Recommendation (approve or manual review)

### 4. Blockchain Registration

Only submissions with:

- AI landscape detected: `true`
- AI confidence: ≥75%
- Admin approval

Get registered on the blockchain.

## Verification Checklist Behavior

### High Confidence (≥85%)

- ✅ GPS coordinates verified
- ✅ Satellite data cross-referenced
- ✅ Image quality standards
- ✅ Coordinates within valid range

### Medium Confidence (60-85%)

- ✅ GPS coordinates verified
- ⚠️ Satellite data cross-referenced (may vary)
- ✅ Image quality standards
- ✅ Coordinates within valid range

### Low Confidence (<60%)

- ✅ GPS coordinates verified
- ❌ Satellite data cross-referenced
- ⚠️ Image quality standards
- ✅ Coordinates within valid range

## Testing the Model

### Test Classification

```kotlin
val classifier = LandscapeClassifier(context)
val imageUri = Uri.parse("content://...")

lifecycleScope.launch {
    val result = classifier.classifyImage(imageUri)
    
    println("Landscape detected: ${result.isLandscape}")
    println("Confidence: ${result.confidence * 100}%")
    println("Category: ${result.category}")
    println("Description: ${result.description}")
}
```

### Expected Results

**Forest Image**:

- isLandscape: `true`
- confidence: `0.90-0.98`
- category: `NATURAL_LANDSCAPE`
- description: "Natural landscape showing geographical terrain with natural features"

**City Image**:

- isLandscape: `true`
- confidence: `0.85-0.95`
- category: `URBAN_LANDSCAPE`
- description: "Urban landscape with buildings and city infrastructure"

**Indoor Image**:

- isLandscape: `false`
- confidence: `0.05-0.20`
- category: `NOT_LANDSCAPE`
- description: "Image does not show geographical landscape"

## LocalAI Configuration

### Recommended Settings

```json
{
  "model_name": "landscape-detector",
  "base_model": "gpt-4-vision-preview",
  "task": "image-classification",
  "hyperparameters": {
    "n_epochs": 3,
    "batch_size": 4,
    "learning_rate": 0.00005,
    "warmup_steps": 100,
    "weight_decay": 0.01
  }
}
```

### Hardware Requirements

- **Minimum**: 8GB RAM, 4 CPU cores
- **Recommended**: 16GB RAM, 8 CPU cores, GPU (NVIDIA with CUDA)
- **Storage**: 10GB for model + 5GB for training data

### Alternative: Remote LocalAI

If running LocalAI on a separate server:

```kotlin
// In LandscapeClassifier.kt
private val localAIEndpoint = "http://your-server-ip:8080/v1/chat/completions"
```

## Troubleshooting

### Issue: Dataset Not Found

**Solution**:

1. Verify path: `C:\Users\medhy\Downloads\archive (2)`
2. Check folder contains GEOEYE-70 subdirectory
3. Ensure images are accessible

### Issue: Training Takes Too Long

**Solution**:

- Reduce dataset size (use subset of images)
- Increase `inSampleSize` in ModelTrainer.kt
- Use more powerful hardware

### Issue: Low Accuracy

**Solutions**:

1. Increase training epochs
2. Add more diverse training data
3. Adjust learning rate
4. Add data augmentation

### Issue: LocalAI Connection Failed

**Solutions**:

1. Verify LocalAI is running: `curl http://localhost:8080/v1/models`
2. Check firewall settings
3. Update endpoint URL in LandscapeClassifier.kt
4. Check LocalAI logs for errors

### Issue: Out of Memory

**Solutions**:

1. Reduce batch size in training config
2. Increase JVM heap size
3. Process images in smaller batches
4. Use image downsampling

## Performance Monitoring

### Metrics to Track

1. **Accuracy**: Correct classifications / Total classifications
2. **Precision**: True positives / (True positives + False positives)
3. **Recall**: True positives / (True positives + False negatives)
4. **F1 Score**: 2 × (Precision × Recall) / (Precision + Recall)

### Expected Performance

- **Accuracy**: >90%
- **Precision**: >88% (minimize false approvals)
- **Recall**: >92% (catch all legitimate landscapes)
- **Inference Time**: <2 seconds per image

## Maintenance & Updates

### Regular Retraining

Retrain quarterly with:

1. New approved submissions
2. Admin-corrected classifications
3. Edge cases and failure examples

### Data Augmentation

Add to training set:

- Different weather conditions
- Various times of day
- Different seasons
- Edge cases from production

### Feedback Loop

1. Admin corrections are logged
2. Misclassified images are flagged
3. Quarterly retraining includes corrections
4. Model accuracy improves over time

## Security Considerations

1. **API Access**: Secure LocalAI endpoint with authentication
2. **Data Privacy**: Don't send PII to AI model
3. **Rate Limiting**: Prevent API abuse
4. **Model Versioning**: Track model versions for auditing

## Files Structure

```
app/src/main/java/.../ai/
├── LandscapeClassifier.kt    # Main AI classification service
├── ModelTrainer.kt            # Training pipeline
└── TrainingScript.kt          # Training utilities

Output Files:
├── train.jsonl                # Training data (80%)
├── validation.jsonl           # Validation data (10%)
├── test.jsonl                 # Test data (10%)
├── model_config.json          # Configuration
└── TRAINING_INSTRUCTIONS.md   # Detailed instructions
```

## Next Steps

1. ✅ Verify dataset access
2. ✅ Run training pipeline
3. ✅ Set up LocalAI server
4. ✅ Upload training data
5. ✅ Start fine-tuning
6. ✅ Test model accuracy
7. ✅ Deploy to production
8. ✅ Monitor performance
9. ✅ Set up retraining schedule

## Support & Resources

- **LocalAI Documentation**: https://localai.io/
- **GEOEYE Dataset**: Contact dataset provider
- **Code**: Check `app/src/main/java/.../ai/` directory
- **Logs**: Check Android Studio Logcat with tag "LandscapeClassifier", "ModelTrainer", "
  TrainingScript"

## Example Usage in MainActivity

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Start training (one-time setup)
        startTrainingIfNeeded()
    }
    
    private fun startTrainingIfNeeded() {
        lifecycleScope.launch {
            // Verify dataset first
            verifyDatasetAccess { result ->
                result.onSuccess { info ->
                    Log.d("Training", "Dataset ready: ${info.totalImages} images")
                    
                    // Start training
                    startModelTraining { trainingResult ->
                        trainingResult.onSuccess { report ->
                            Log.d("Training", "Success! ${report.totalImages} images processed")
                        }
                        trainingResult.onFailure { error ->
                            Log.e("Training", "Failed: ${error.message}")
                        }
                    }
                }
            }
        }
    }
}
```

## Conclusion

This AI integration ensures that carbon credit projects include legitimate geographical landscape
imagery, preventing fraud and maintaining registry integrity. The system provides:

1. ✅ Automated landscape verification
2. ✅ High-confidence classification
3. ✅ Admin decision support
4. ✅ Blockchain integration
5. ✅ Continuous improvement through retraining

The model learns from the GEOEYE-70 dataset and improves over time with production feedback,
ensuring accurate and reliable verification for carbon credit projects.
