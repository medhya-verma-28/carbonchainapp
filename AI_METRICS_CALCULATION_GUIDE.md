# AI Metrics Calculation Guide

## Overview

The AI landscape detection system now calculates three critical metrics for carbon registry
verification:

1. **CO2 Value** (tons) - Carbon sequestration potential
2. **Hectares Value** (ha) - Visible land area
3. **Vegetation Coverage** (%) - Green vegetation percentage

## ⚠️ Important Rule: Non-Landscape Images = Zero Values

**If the image is NOT a landscape (indoor, portrait, product, etc.):**

- CO2 = **0.0 tons**
- Hectares = **0.0 ha**
- Vegetation = **0.0%**

This ensures that only legitimate geographical areas can generate carbon credits.

---

## CO2 Value Calculation (tons)

Estimates potential carbon sequestration based on landscape type and visible area.

### Calculation Formula

```
CO2 (tons) = Base Rate × Visible Hectares × Vegetation Factor
```

### Base Rates by Landscape Type

| Landscape Type | CO2 per Hectare | Example |
|----------------|-----------------|---------|
| **Dense Forests** | 2.0 - 5.0 tons | Old-growth rainforest, mature woodland |
| **Light Forests** | 1.0 - 2.5 tons | Young forest, sparse woodland |
| **Agricultural Land** | 0.5 - 1.5 tons | Cropland, pastures, plantations |
| **Urban Landscapes** | 0.1 - 0.5 tons | Cities with parks, green spaces |
| **Desert/Barren** | 0.0 - 0.2 tons | Arid land, minimal vegetation |
| **Not Landscape** | **0.0 tons** | Indoor, portrait, product, etc. |

### Examples

**Example 1: Dense Forest**

- Image shows: Dense tropical forest
- Visible area: 2.0 hectares
- Vegetation: 85%
- **CO2 = 3.5 tons** (high sequestration)

**Example 2: Urban Park**

- Image shows: City with parks
- Visible area: 1.0 hectare
- Vegetation: 25%
- **CO2 = 0.3 tons** (limited green space)

**Example 3: Indoor Office**

- Image shows: Office interior
- AI detects: NOT a landscape
- **CO2 = 0.0 tons** ❌

---

## Hectares Value Calculation (ha)

Estimates the visible geographical area in the image.

### Estimation Guidelines

| View Type | Hectares Range | How AI Determines |
|-----------|----------------|-------------------|
| **Aerial/Satellite** | 0.5 - 5.0 ha | High altitude view, large visible area |
| **Ground-Level Landscape** | 0.2 - 2.0 ha | Standing on ground, horizon visible |
| **Close-Up View** | 0.1 - 0.5 ha | Limited field of view |
| **Urban Scene** | 0.3 - 1.5 ha | Buildings and streets visible |
| **Not Landscape** | **0.0 ha** | No geographical area |

### Calculation Factors

AI considers:

- **Perspective**: Aerial vs ground-level
- **Horizon**: Visible or not
- **Scale indicators**: Buildings, trees, roads
- **Zoom level**: Satellite imagery zoom
- **Visible boundaries**: How much land is shown

### Examples

**Example 1: Satellite Forest View**

- Perspective: High altitude
- Multiple tree clusters visible
- **Hectares = 3.2 ha** (large area visible)

**Example 2: Ground-Level Farm**

- Perspective: Ground level
- Single field visible
- **Hectares = 0.8 ha** (limited view)

**Example 3: Product Photo**

- Perspective: Close-up object
- AI detects: NOT a landscape
- **Hectares = 0.0 ha** ❌

---

## Vegetation Coverage Calculation (%)

Estimates percentage of green vegetation/plants in visible area.

### Coverage Categories

| Coverage Range | Description | Landscape Type |
|----------------|-------------|----------------|
| **80-100%** | Dense vegetation | Rainforest, dense forest |
| **50-80%** | Mixed vegetation | Mixed forest, green fields |
| **40-90%** | Agricultural | Croplands (varies by season) |
| **10-40%** | Light vegetation | Urban parks, sparse vegetation |
| **0-20%** | Minimal | Desert, barren land, concrete |
| **0%** | None | **Not landscape** ❌ |

### Calculation Method

AI analyzes:

- **Green pixels**: Percentage of image with vegetation color
- **Texture patterns**: Leaf/tree textures vs buildings/ground
- **Color analysis**: Green spectrum detection
- **Coverage density**: Sparse vs dense vegetation

### Examples

**Example 1: Dense Forest**

- Visual: Mostly green, tree canopy
- **Vegetation = 88%** (dense coverage)

**Example 2: Urban Cityscape**

- Visual: Mostly buildings, some parks
- **Vegetation = 18%** (limited greenery)

**Example 3: Desert Landscape**

- Visual: Sand, rocks, minimal plants
- **Vegetation = 5%** (very sparse)

**Example 4: Indoor Scene**

- Visual: Office interior
- AI detects: NOT a landscape
- **Vegetation = 0.0%** ❌

---

## How AI Analyzes Images

### Step 1: Landscape Detection

```
Is this a geographical area?
├─ YES → Continue to metric calculations
└─ NO  → Return all 0 values
```

### Step 2: Category Classification

AI identifies landscape type:

- Natural Landscape (forests, mountains, rivers)
- Urban Landscape (cities, buildings)
- Agricultural Landscape (farms, fields)
- Coastal Landscape (beaches, shores)
- Desert Landscape (arid regions)

### Step 3: Metric Calculation

For each metric, AI:

1. **Analyzes image features**
    - Colors, textures, patterns
    - Scale, perspective, zoom level
    - Visible landmarks, horizon

2. **Applies estimation algorithms**
    - CO2: Base rate × area × vegetation factor
    - Hectares: Perspective + scale indicators
    - Vegetation: Green pixel analysis

3. **Returns calculated values**
    - Numerical values with appropriate ranges
    - 0 values if not a landscape

---

## Integration with Carbon Registry

### User Upload Flow

```
1. User uploads image
   ↓
2. AI analyzes image
   ↓
3. AI calculates metrics:
   - isLandscape: true/false
   - CO2: 0.0-10.0 tons
   - Hectares: 0.0-10.0 ha
   - Vegetation: 0.0-100.0%
   ↓
4. Metrics displayed in Analysis Summary
   ↓
5. Admin reviews with AI recommendations
```

### Admin Verification

Admins see in "Analysis Summary":

```
┌─────────────────────────────────┐
│     Analysis Summary            │
├─────────────────────────────────┤
│  CO₂: 2.84 tons                │  ← AI calculated
│  Hectares: 1.75 ha             │  ← AI calculated
│  Vegetation: 76.55%            │  ← AI calculated
│  AI Confidence: 88.0%          │  ← AI confidence
└─────────────────────────────────┘
```

**For Non-Landscape Images:**

```
┌─────────────────────────────────┐
│     Analysis Summary            │
├─────────────────────────────────┤
│  CO₂: 0.0 tons                 │  ← Zero (not landscape)
│  Hectares: 0.0 ha              │  ← Zero (not landscape)
│  Vegetation: 0.0%              │  ← Zero (not landscape)
│  AI Confidence: 12.0%          │  ← Low confidence
│  ⚠️ Manual review required      │
└─────────────────────────────────┘
```

---

## Validation Rules

### Approval Criteria

For blockchain registration, submission must meet:

1. ✅ **AI Landscape Detected** = `true`
2. ✅ **AI Confidence** ≥ `75%`
3. ✅ **CO2 Value** > `0.0` tons
4. ✅ **Hectares Value** > `0.0` ha
5. ✅ **Admin Approval** = `approved`

### Rejection Criteria

Submission will be flagged if:

1. ❌ **AI Landscape Detected** = `false`
2. ❌ **AI Confidence** < `60%`
3. ❌ **CO2 = 0.0** (indicates non-landscape)
4. ❌ **Hectares = 0.0** (indicates non-landscape)
5. ❌ **Vegetation = 0.0** (indicates non-landscape)

---

## Example Scenarios

### Scenario 1: Valid Forest Submission ✅

**Image**: Dense forest photograph

**AI Analysis**:

- isLandscape: `true`
- Category: `NATURAL_LANDSCAPE`
- Confidence: `91%`

**Calculated Metrics**:

- CO₂: `3.2 tons`
- Hectares: `1.8 ha`
- Vegetation: `85%`

**Result**: ✅ Approved → Blockchain registration

---

### Scenario 2: Invalid Indoor Photo ❌

**Image**: Office interior

**AI Analysis**:

- isLandscape: `false`
- Category: `NOT_LANDSCAPE`
- Confidence: `8%`

**Calculated Metrics**:

- CO₂: `0.0 tons` ❌
- Hectares: `0.0 ha` ❌
- Vegetation: `0.0%` ❌

**Result**: ❌ Rejected → Manual review required

---

### Scenario 3: Urban Park (Marginal) ⚠️

**Image**: City with parks

**AI Analysis**:

- isLandscape: `true`
- Category: `URBAN_LANDSCAPE`
- Confidence: `72%`

**Calculated Metrics**:

- CO₂: `0.4 tons`
- Hectares: `0.9 ha`
- Vegetation: `22%`

**Result**: ⚠️ Manual review → Admin decides

---

## Technical Implementation

### LocalAI Prompt

The AI receives detailed instructions:

```
For LANDSCAPE images, estimate:

1. CO2 Value (tons): Based on landscape type
   - Dense forests: 2.0-5.0 tons/ha
   - Light forests: 1.0-2.5 tons/ha
   - Agricultural: 0.5-1.5 tons/ha
   - Urban: 0.1-0.5 tons
   - Desert: 0.0-0.2 tons
   - NOT landscape: 0.0 tons

2. Hectares Value: Based on perspective
   - Aerial view: 0.5-5.0 ha
   - Ground level: 0.2-2.0 ha
   - Close-up: 0.1-0.5 ha
   - NOT landscape: 0.0 ha

3. Vegetation Coverage (%): Based on greenery
   - Dense forest: 80-100%
   - Mixed: 50-80%
   - Agricultural: 40-90%
   - Urban: 10-40%
   - Desert: 0-20%
   - NOT landscape: 0.0%

IMPORTANT: If NOT landscape, all values = 0.0
```

### Response Format

```json
{
  "isLandscape": true,
  "confidence": 0.88,
  "category": "NATURAL_LANDSCAPE",
  "description": "Dense forest with high vegetation",
  "co2Value": 3.2,
  "hectaresValue": 1.8,
  "vegetationCoverage": 85.0
}
```

### Code Implementation

```kotlin
// AI calculates values
val result = landscapeClassifier.classifyImage(imageUri)

// Non-landscape images automatically get 0 values
if (!result.isLandscape) {
    // All metrics are 0.0
    co2Value = 0.0
    hectaresValue = 0.0
    vegetationCoverage = 0.0
}

// Use in submission
val submission = UserSubmission(
    co2Value = result.co2Value,      // AI calculated or 0.0
    hectaresValue = result.hectaresValue,  // AI calculated or 0.0
    vegetationCoverage = result.vegetationCoverage  // AI calculated or 0.0
)
```

---

## Benefits

### 1. Fraud Prevention

- Non-landscape images cannot generate carbon credits
- All metrics must be > 0 for approval

### 2. Accurate Metrics

- AI-based estimation more consistent than manual
- Considers landscape type, area, vegetation

### 3. Admin Efficiency

- Clear metrics for decision-making
- AI recommendations guide approval

### 4. Data Quality

- Only legitimate landscapes on blockchain
- Accurate carbon credit calculations

---

## Summary

| Metric | Landscape Image | Non-Landscape Image |
|--------|----------------|---------------------|
| **CO₂** | 0.1 - 5.0 tons | **0.0 tons** ❌ |
| **Hectares** | 0.1 - 5.0 ha | **0.0 ha** ❌ |
| **Vegetation** | 0 - 100% | **0.0%** ❌ |
| **Confidence** | 60 - 100% | 0 - 60% |
| **Approval** | ✅ Possible | ❌ Rejected |

**Key Principle**: Only geographical landscape images can generate non-zero values for CO2,
hectares, and vegetation.

---

For more details, see:

- `AI_MODEL_TRAINING_GUIDE.md` - Complete AI training guide
- `LandscapeClassifier.kt` - Implementation code
- `AdminVerificationScreen.kt` - UI display
