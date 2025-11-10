# Quick Start: AI Model Training

## Prerequisites

1. ✅ Dataset downloaded at: `C:\Users\medhy\Downloads\archive (2)`
2. ✅ Android Studio project open
3. ✅ Device/emulator running (for training script execution)

## Option 1: Train from Android App (Easiest)

### Step 1: Add Training Button to MainActivity

Add this code to your MainActivity.kt:

```kotlin
// Add to MainActivity imports
import com.runanywhere.startup_hackathon20.ai.startModelTraining
import com.runanywhere.startup_hackathon20.ai.verifyDatasetAccess
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.widget.Toast

// Add this function to MainActivity class
private fun initializeAITraining() {
    lifecycleScope.launch {
        // First, verify dataset is accessible
        verifyDatasetAccess(
            datasetPath = "C:\\Users\\medhy\\Downloads\\archive (2)"
        ) { verifyResult ->
            verifyResult.onSuccess { info ->
                Log.d("AI Training", "✅ Dataset verified: ${info.totalImages} images found")
                Toast.makeText(
                    this@MainActivity,
                    "Dataset ready: ${info.totalImages} images",
                    Toast.LENGTH_LONG
                ).show()
                
                // Now start training
                startTraining()
            }
            verifyResult.onFailure { error ->
                Log.e("AI Training", "❌ Dataset verification failed: ${error.message}")
                Toast.makeText(
                    this@MainActivity,
                    "Dataset not found: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}

private fun startTraining() {
    lifecycleScope.launch {
        Toast.makeText(
            this@MainActivity,
            "🚀 Training started... Check Logcat for progress",
            Toast.LENGTH_LONG
        ).show()
        
        startModelTraining(
            datasetPath = "C:\\Users\\medhy\\Downloads\\archive (2)",
            outputPath = null // Will use app's external storage
        ) { trainingResult ->
            trainingResult.onSuccess { report ->
                Log.d("AI Training", "✅ Training completed!")
                Log.d("AI Training", report.toString())
                
                Toast.makeText(
                    this@MainActivity,
                    "Training complete! ${report.totalImages} images processed",
                    Toast.LENGTH_LONG
                ).show()
                
                // Show output location
                Log.d("AI Training", "\n📁 Training files saved to:")
                Log.d("AI Training", report.outputPath)
                Log.d("AI Training", "\n📋 Next steps:")
                Log.d("AI Training", "1. Copy training files from device")
                Log.d("AI Training", "2. Set up LocalAI server")
                Log.d("AI Training", "3. Upload files to LocalAI")
                Log.d("AI Training", "4. Start fine-tuning")
            }
            trainingResult.onFailure { error ->
                Log.e("AI Training", "❌ Training failed: ${error.message}", error)
                Toast.makeText(
                    this@MainActivity,
                    "Training failed: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}

// Call this in onCreate or from a button click
// initializeAITraining()
```

### Step 2: Run Training

1. Build and run the app
2. Call `initializeAITraining()` (from a button or on app start)
3. Watch Logcat for progress:
   ```
   Progress: 10% - Cleaning dataset...
   Progress: 20% - Splitting dataset...
   Progress: 30% - Generating training data...
   ...
   Progress: 100% - Training preparation complete!
   ```

### Step 3: Get Training Files

Training files will be saved to:

```
/sdcard/Android/data/com.runanywhere.startup_hackathon20/files/landscape_training/
```

Use Android Device File Explorer or adb to copy files:

```bash
adb pull /sdcard/Android/data/com.runanywhere.startup_hackathon20/files/landscape_training/ ./training_data/
```

## Option 2: Direct Training (Advanced)

If you have direct access to the dataset on your development machine:

### Step 1: Create Training Script

Create `train_model.py`:

```python
import json
import os
from pathlib import Path

# Configuration
DATASET_PATH = r"C:\Users\medhy\Downloads\archive (2)"
OUTPUT_PATH = "./landscape_training"

def count_images():
    """Count total images in dataset"""
    extensions = {'.jpg', '.jpeg', '.png', '.tif', '.tiff'}
    count = 0
    
    for root, dirs, files in os.walk(DATASET_PATH):
        for file in files:
            if Path(file).suffix.lower() in extensions:
                count += 1
    
    return count

def main():
    print("🚀 Starting AI Model Training")
    print(f"📁 Dataset: {DATASET_PATH}")
    print(f"📁 Output: {OUTPUT_PATH}")
    
    # Verify dataset
    if not os.path.exists(DATASET_PATH):
        print("❌ Dataset not found!")
        return
    
    total_images = count_images()
    print(f"✅ Found {total_images} images")
    
    # Create output directory
    os.makedirs(OUTPUT_PATH, exist_ok=True)
    
    print("\n⚠️  Note: Use the Android app method for actual training")
    print("   This requires Android context for image processing")

if __name__ == "__main__":
    main()
```

## Option 3: Use Pre-built Training Data

If training takes too long, you can use a subset:

```kotlin
// Modify TrainingScript.kt to use subset
suspend fun trainWithSubset(context: Context, maxImages: Int = 1000) {
    // Same as normal training but limit dataset size
    // Implementation already handles large datasets efficiently
}
```

## Monitoring Training Progress

### Watch Logcat

Filter by tags:

- `TrainingScript`: Main training progress
- `ModelTrainer`: Detailed training steps
- `LandscapeClassifier`: Classification tests

### Expected Timeline

For ~34,602 images:

- Cleaning dataset: ~2-3 minutes
- Splitting dataset: <1 minute
- Generating training data: ~30-60 minutes (depends on hardware)
- Generating validation data: ~5-10 minutes
- Generating test data: ~5-10 minutes
- Creating config: <1 minute

**Total**: ~45-75 minutes

### Speed Optimization

To speed up training:

1. **Reduce dataset size** (use first 5000 images):
   ```kotlin
   // In ModelTrainer.kt, add limit
   .take(5000)
   ```

2. **Increase downsampling**:
   ```kotlin
   // In ModelTrainer.kt
   inSampleSize = 4 // Instead of 2
   ```

3. **Use faster device**: Training on high-end phone will be faster

## Verifying Training Output

After training completes, check these files exist:

```bash
cd /sdcard/Android/data/com.runanywhere.startup_hackathon20/files/landscape_training/

ls -lh
# Expected output:
# train.jsonl (largest file, ~80% of data)
# validation.jsonl (~10% of data)
# test.jsonl (~10% of data)
# model_config.json (small config file)
# TRAINING_INSTRUCTIONS.md (documentation)
```

## Setting Up LocalAI

### Quick Setup with Docker

```bash
# Pull and run LocalAI
docker run -d -p 8080:8080 \
  --name localai \
  -v $PWD/models:/models \
  -v $PWD/training_data:/build/training \
  localai/localai:latest

# Verify it's running
curl http://localhost:8080/readiness
```

### Upload Training Files

```bash
# Copy training files to LocalAI container
docker cp train.jsonl localai:/build/training/
docker cp validation.jsonl localai:/build/training/
docker cp test.jsonl localai:/build/training/
```

### Start Fine-tuning

```bash
curl -X POST http://localhost:8080/v1/fine-tuning/jobs \
  -H "Content-Type: application/json" \
  -d '{
    "training_file": "/build/training/train.jsonl",
    "validation_file": "/build/training/validation.jsonl",
    "model": "gpt-4-vision-preview",
    "hyperparameters": {
      "n_epochs": 3,
      "batch_size": 4,
      "learning_rate": 0.00005
    }
  }'
```

Save the job_id from response, then monitor:

```bash
# Check status
curl http://localhost:8080/v1/fine-tuning/jobs/<job_id>
```

## Testing the Model

Once training is complete:

```kotlin
// In your app
val classifier = LandscapeClassifier(context)
val testImageUri = Uri.parse("content://...")

lifecycleScope.launch {
    val result = classifier.classifyImage(testImageUri)
    
    Log.d("Test", "Landscape: ${result.isLandscape}")
    Log.d("Test", "Confidence: ${result.confidence * 100}%")
    Log.d("Test", "Category: ${result.category}")
    Log.d("Test", "Description: ${result.description}")
}
```

## Troubleshooting

### Dataset Not Found

- Verify exact path: `C:\Users\medhy\Downloads\archive (2)`
- Check folder has GEOEYE-70 subdirectory
- On Android, use storage permissions

### Out of Memory

- Reduce batch size
- Increase inSampleSize
- Process fewer images at once

### Training Too Slow

- Use subset of images (first 1000-5000)
- Increase inSampleSize from 2 to 4
- Use more powerful device

### LocalAI Connection Failed

- Check LocalAI is running: `docker ps`
- Verify port 8080 is accessible
- Update endpoint in LandscapeClassifier.kt

## Integration Checklist

After training completes:

- [ ] Training files generated successfully
- [ ] LocalAI server running
- [ ] Fine-tuning job started
- [ ] Model accuracy tested (>90%)
- [ ] LocalAI endpoint configured in app
- [ ] CarbonRepository initialized with classifier
- [ ] Admin UI shows AI verification section
- [ ] Test upload with real image
- [ ] Verify AI results in admin portal
- [ ] Check verification markers update correctly

## Next Steps

1. ✅ Complete training
2. ✅ Set up LocalAI
3. ✅ Test model accuracy
4. ✅ Integrate with app
5. ✅ Test end-to-end flow
6. ✅ Deploy to production
7. ✅ Monitor performance

## Support

For issues:

1. Check Logcat logs
2. Review `AI_MODEL_TRAINING_GUIDE.md`
3. Verify dataset integrity
4. Test with smaller dataset first

---

**Ready to train?** Just add the `initializeAITraining()` function to your MainActivity and run it!
