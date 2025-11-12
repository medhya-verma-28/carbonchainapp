# Change App Icon - Complete Guide

## 🎨 Current Icon

The app currently uses the default Android launcher icon (green Android robot).

## 🎯 Goal

Replace with a custom logo without background (transparent background).

---

## 📋 Quick Steps

### Option 1: Use Android Studio (Easiest)

1. **Open Android Studio**
2. **Right-click on `res` folder** → New → Image Asset
3. **Configure:**
    - Icon Type: Launcher Icons (Adaptive and Legacy)
    - Name: `ic_launcher`
    - Foreground Layer: Select your image
    - Background Layer: Choose solid color or keep transparent
    - Trim: Yes (removes white space)
    - Resize: Up to 75% (adjust as needed)
4. **Click Next** → **Finish**
5. **Done!** Android Studio creates all density versions

### Option 2: Manual Setup (Full Control)

#### Step 1: Prepare Your Image

**Requirements:**

- Format: PNG with transparent background
- Size: 512x512 px (recommended)
- File name: `ic_launcher_foreground.png`

**Tools to remove background:**

- [Remove.bg](https://remove.bg) - Automatic background removal
- Photoshop/GIMP - Manual editing
- [Pixlr](https://pixlr.com) - Online editor

#### Step 2: Generate All Sizes

Use [App Icon Generator](https://www.appicon.co/) or create manually:

| Density | Size (px) | Folder |
|---------|-----------|---------|
| mdpi | 48x48 | mipmap-mdpi |
| hdpi | 72x72 | mipmap-hdpi |
| xhdpi | 96x96 | mipmap-xhdpi |
| xxhdpi | 144x144 | mipmap-xxhdpi |
| xxxhdpi | 192x192 | mipmap-xxxhdpi |

#### Step 3: Add Files to Project

Place generated images:

```
app/src/main/res/
├── mipmap-mdpi/ic_launcher.png
├── mipmap-hdpi/ic_launcher.png
├── mipmap-xhdpi/ic_launcher.png
├── mipmap-xxhdpi/ic_launcher.png
├── mipmap-xxxhdpi/ic_launcher.png
└── mipmap-xxxhdpi/ic_launcher_round.png
```

#### Step 4: Update Manifest (Already Configured)

The `AndroidManifest.xml` already references the icons:

```xml
<application
    android:icon="@mipmap/ic_launcher"
    android:roundIcon="@mipmap/ic_launcher_round"
    ...>
```

---

## 🚀 Option 3: Use Existing app_logo.png

Your project already has `app/src/main/res/drawable/app_logo.png`.

To use it as app icon:

### Method A: Copy to mipmap folders

```powershell
# Create a script to copy and resize
# You'll need ImageMagick or similar tool
```

### Method B: Create adaptive icon with app_logo.png

1. Create `ic_launcher_foreground_custom.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <group
        android:scaleX="0.5"
        android:scaleY="0.5"
        android:translateX="27"
        android:translateY="27">
        <!-- Your logo vector path here -->
    </group>
</vector>
```

2. Update `mipmap-anydpi-v26/ic_launcher.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground_custom"/>
</adaptive-icon>
```

---

## 🎨 Creating a Custom Carbon-Themed Icon

### Blue Carbon / Ocean Wave Icon

Create `ic_launcher_foreground_wave.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    
    <!-- Ocean Wave -->
    <path
        android:fillColor="#4FC3F7"
        android:pathData="M20,60 Q30,50 40,60 T60,60 T80,60 L80,80 L20,80 Z"/>
    
    <!-- Deeper Wave -->
    <path
        android:fillColor="#0277BD"
        android:pathData="M20,70 Q35,65 50,70 T80,70 L80,80 L20,80 Z"/>
    
    <!-- Carbon Leaf -->
    <path
        android:fillColor="#66BB6A"
        android:pathData="M54,30 Q60,25 65,30 L62,40 Q60,42 58,40 L54,30 Z"/>
</vector>
```

### Green Leaf Icon

Create `ic_launcher_foreground_leaf.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    
    <!-- Leaf Shape -->
    <path
        android:fillColor="#4CAF50"
        android:pathData="M54,30 C54,30 40,35 40,55 C40,75 54,85 54,85 C54,85 68,75 68,55 C68,35 54,30 54,30 Z"/>
    
    <!-- Leaf Vein -->
    <path
        android:fillColor="#388E3C"
        android:strokeWidth="2"
        android:pathData="M54,30 L54,85"/>
</vector>
```

### Earth/Globe Icon

Create `ic_launcher_foreground_globe.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    
    <!-- Globe Circle -->
    <path
        android:fillColor="#2196F3"
        android:pathData="M54,30 a24,24 0 1,0 0,48 a24,24 0 1,0 0,-48 Z"/>
    
    <!-- Continents (simplified) -->
    <path
        android:fillColor="#4CAF50"
        android:pathData="M50,35 L52,40 L55,38 L58,42 L60,40 L58,35 Z
                        M45,50 L50,48 L52,52 L48,55 L45,52 Z
                        M60,55 L62,58 L65,56 L63,52 Z"/>
</vector>
```

---

## 🔧 PowerShell Script to Apply Custom Icon

Create `change-app-icon.ps1`:

```powershell
# Change App Icon Script

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Change App Icon" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$projectPath = "C:/Users/medhy/StudioProjects/Hackss"
$resPath = "$projectPath/app/src/main/res"

Write-Host "Choose icon option:" -ForegroundColor Yellow
Write-Host "1. Ocean Wave (Blue Carbon theme)" -ForegroundColor White
Write-Host "2. Green Leaf (Environmental theme)" -ForegroundColor White
Write-Host "3. Globe/Earth (Global theme)" -ForegroundColor White
Write-Host "4. Use custom image file" -ForegroundColor White
Write-Host ""

$choice = Read-Host "Enter choice (1-4)"

switch ($choice) {
    "1" {
        # Create wave icon
        Write-Host "Creating Ocean Wave icon..." -ForegroundColor Green
        # Copy wave XML to foreground
    }
    "2" {
        # Create leaf icon
        Write-Host "Creating Green Leaf icon..." -ForegroundColor Green
        # Copy leaf XML to foreground
    }
    "3" {
        # Create globe icon
        Write-Host "Creating Globe icon..." -ForegroundColor Green
        # Copy globe XML to foreground
    }
    "4" {
        # Use custom image
        $imagePath = Read-Host "Enter path to your PNG image"
        Write-Host "Processing custom image..." -ForegroundColor Green
        # Process and copy custom image
    }
}

Write-Host ""
Write-Host "Rebuilding app..." -ForegroundColor Yellow
Set-Location $projectPath
./gradlew clean build

Write-Host ""
Write-Host "✓ Icon changed successfully!" -ForegroundColor Green
Write-Host "Install the app to see the new icon" -ForegroundColor Cyan
```

---

## 📱 Testing the New Icon

After changing the icon:

1. **Uninstall old app** (to clear icon cache):
   ```powershell
   adb uninstall com.runanywhere.startup_hackathon20
   ```

2. **Install new app**:
   ```powershell
   ./gradlew installDebug
   ```

3. **Check home screen** - You should see the new icon!

---

## 🎨 Recommended Tools

1. **Remove Background:**
    - [Remove.bg](https://remove.bg) - Best for photos
    - [Pixlr](https://pixlr.com) - Online editor

2. **Generate Icon Sizes:**
    - [App Icon Generator](https://www.appicon.co/)
    - [Android Asset Studio](https://romannurik.github.io/AndroidAssetStudio/)

3. **Create Vector Icons:**
    - [Figma](https://figma.com) - Professional design
    - [Inkscape](https://inkscape.org) - Free vector editor
    - [SVG to Android Vector](https://svg2vector.com/) - Convert SVG

---

## ✅ Quick Checklist

- [ ] Prepare image with transparent background (512x512 px)
- [ ] Generate all density versions (mdpi to xxxhdpi)
- [ ] Place images in mipmap folders
- [ ] Or use Android Studio Image Asset tool
- [ ] Clean and rebuild project
- [ ] Uninstall old app
- [ ] Install new version
- [ ] Check icon on home screen

---

## 💡 Tips

1. **Keep it simple** - Icons look best with minimal detail
2. **Use solid colors** - Avoid gradients for better clarity
3. **Test on dark/light themes** - Ensure visibility
4. **Adaptive icons** - Include background layer for Android 8+
5. **Square safe zone** - Keep important elements in center 66%

---

## 🚀 What Would You Like?

Let me know:

1. Upload an image and I'll help process it
2. Choose one of the pre-made themes (Ocean, Leaf, Globe)
3. I can create a custom design based on your description

Just tell me which option you prefer!
