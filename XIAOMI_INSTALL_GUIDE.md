# Install and Run on Xiaomi Phone Guide

## Prerequisites

- ✅ Xiaomi phone with USB debugging enabled
- ✅ USB cable connected to computer
- ✅ Android Studio installed
- ✅ ADB drivers installed

## Quick Install (Automated)

### Option 1: Using PowerShell Script

Run this command in PowerShell from project root:

```powershell
.\install-xiaomi.ps1
```

### Option 2: Manual Steps

Follow the steps below if automated script doesn't work.

## Step-by-Step Installation

### 1. Enable Developer Options on Xiaomi Phone

1. Open **Settings**
2. Go to **About phone**
3. Tap **MIUI version** 7 times
4. You'll see "You are now a developer!"

### 2. Enable USB Debugging

1. Go back to **Settings**
2. Open **Additional settings**
3. Select **Developer options**
4. Enable **USB debugging**
5. Enable **Install via USB** (if available)
6. Enable **USB debugging (Security settings)** (if available)

### 3. Connect Phone to Computer

1. Connect Xiaomi phone via USB cable
2. On phone, select **File Transfer** mode
3. Allow USB debugging when prompted
4. Check "Always allow from this computer"

### 4. Verify ADB Connection

Open PowerShell in project directory:

```powershell
# Check if device is connected
adb devices

# You should see something like:
# List of devices attached
# a1b2c3d4        device
```

If device not found:

```powershell
# Restart ADB server
adb kill-server
adb start-server
adb devices
```

### 5. Uninstall Old Version (if exists)

```powershell
adb uninstall com.runanywhere.startup_hackathon20
```

### 6. Build and Install App

#### Method A: Using Gradle (Recommended)

```powershell
# Clean build
.\gradlew clean

# Build debug APK
.\gradlew assembleDebug

# Install on connected device
.\gradlew installDebug

# Or do all in one command
.\gradlew clean assembleDebug installDebug
```

#### Method B: Using Android Studio

1. Open project in Android Studio
2. Wait for Gradle sync to complete
3. Select your Xiaomi device from device dropdown (top toolbar)
4. Click **Run** button (green play icon) or press `Shift+F10`

#### Method C: Manual APK Installation

```powershell
# Build APK
.\gradlew assembleDebug

# Install APK
adb install -r app\build\outputs\apk\debug\app-debug.apk

# -r flag reinstalls keeping app data
```

### 7. Grant Permissions on Xiaomi

After installation, grant required permissions:

```powershell
# Grant all permissions at once
adb shell pm grant com.runanywhere.startup_hackathon20 android.permission.CAMERA
adb shell pm grant com.runanywhere.startup_hackathon20 android.permission.ACCESS_FINE_LOCATION
adb shell pm grant com.runanywhere.startup_hackathon20 android.permission.ACCESS_COARSE_LOCATION
adb shell pm grant com.runanywhere.startup_hackathon20 android.permission.READ_EXTERNAL_STORAGE
adb shell pm grant com.runanywhere.startup_hackathon20 android.permission.WRITE_EXTERNAL_STORAGE
adb shell pm grant com.runanywhere.startup_hackathon20 android.permission.READ_MEDIA_IMAGES
```

### 8. Launch App

```powershell
# Launch app
adb shell am start -n com.runanywhere.startup_hackathon20/.MainActivity

# Or launch from phone home screen
```

## Xiaomi-Specific Issues & Solutions

### Issue 1: "Installation Blocked by MIUI"

**Solution**:

1. Go to **Settings** → **Additional settings** → **Privacy**
2. Select **Install apps from external sources**
3. Enable for **USB debugging** or **ADB**

### Issue 2: "App Not Installed" Error

**Solution**:

```powershell
# Completely remove old version
adb uninstall com.runanywhere.startup_hackathon20

# Clear package installer cache
adb shell pm clear com.miui.packageinstaller

# Retry installation
.\gradlew installDebug
```

### Issue 3: Permissions Not Working

**Solution**:

1. Open app on phone
2. Go to **Settings** → **Apps** → **Startup Hackathon**
3. Select **Permissions**
4. Manually enable all permissions

Or via ADB:

```powershell
# Reset permissions
adb shell pm reset-permissions com.runanywhere.startup_hackathon20

# Grant again
adb shell pm grant com.runanywhere.startup_hackathon20 android.permission.CAMERA
# ... (repeat for other permissions)
```

### Issue 4: "Device Unauthorized"

**Solution**:

```powershell
# Revoke USB debugging authorizations on phone
# Settings → Additional settings → Developer options → Revoke USB debugging authorizations

# Reconnect and accept prompt again
adb kill-server
adb start-server
adb devices
```

### Issue 5: MIUI Optimization Blocking App

**Solution**:

1. Go to **Developer options**
2. Disable **MIUI optimization**
3. Restart phone
4. Reinstall app

### Issue 6: App Crashes on Launch

**Solution**:

```powershell
# Check logcat for errors
adb logcat | Select-String "runanywhere"

# Or save logs to file
adb logcat > app_logs.txt
```

## Xiaomi MIUI Permissions

MIUI has additional security layers. Grant these manually:

1. **Autostart**:
    - Settings → Apps → Startup Hackathon → Autostart → Enable

2. **Background Restrictions**:
    - Settings → Apps → Startup Hackathon → Battery saver → No restrictions

3. **Display Pop-up Windows**:
    - Settings → Apps → Startup Hackathon → Display pop-up windows → Enable

## Verify Installation

```powershell
# Check if app is installed
adb shell pm list packages | Select-String "runanywhere"

# Get app info
adb shell dumpsys package com.runanywhere.startup_hackathon20 | Select-String "versionName"

# Check app permissions
adb shell dumpsys package com.runanywhere.startup_hackathon20 | Select-String "permission"
```

## Complete Reinstall Script

Save this as `reinstall.ps1`:

```powershell
Write-Host "=== Xiaomi App Reinstall Script ===" -ForegroundColor Cyan

# 1. Check ADB connection
Write-Host "`n1. Checking ADB connection..." -ForegroundColor Yellow
adb devices

# 2. Uninstall old version
Write-Host "`n2. Uninstalling old version..." -ForegroundColor Yellow
adb uninstall com.runanywhere.startup_hackathon20

# 3. Clean build
Write-Host "`n3. Cleaning project..." -ForegroundColor Yellow
.\gradlew clean

# 4. Build APK
Write-Host "`n4. Building APK..." -ForegroundColor Yellow
.\gradlew assembleDebug

# 5. Install APK
Write-Host "`n5. Installing APK..." -ForegroundColor Yellow
.\gradlew installDebug

# 6. Grant permissions
Write-Host "`n6. Granting permissions..." -ForegroundColor Yellow
adb shell pm grant com.runanywhere.startup_hackathon20 android.permission.CAMERA
adb shell pm grant com.runanywhere.startup_hackathon20 android.permission.ACCESS_FINE_LOCATION
adb shell pm grant com.runanywhere.startup_hackathon20 android.permission.ACCESS_COARSE_LOCATION
adb shell pm grant com.runanywhere.startup_hackathon20 android.permission.READ_EXTERNAL_STORAGE
adb shell pm grant com.runanywhere.startup_hackathon20 android.permission.WRITE_EXTERNAL_STORAGE
adb shell pm grant com.runanywhere.startup_hackathon20 android.permission.READ_MEDIA_IMAGES

# 7. Launch app
Write-Host "`n7. Launching app..." -ForegroundColor Yellow
adb shell am start -n com.runanywhere.startup_hackathon20/.MainActivity

Write-Host "`n=== Installation Complete! ===" -ForegroundColor Green
Write-Host "Check your Xiaomi phone now." -ForegroundColor Green
```

Run with:

```powershell
.\reinstall.ps1
```

## Debugging on Xiaomi

### View Real-time Logs

```powershell
# Filter by app package
adb logcat | Select-String "runanywhere"

# Filter by priority (Error, Warning)
adb logcat *:E | Select-String "runanywhere"

# Clear logs and start fresh
adb logcat -c
adb logcat | Select-String "runanywhere"
```

### Check App Status

```powershell
# Check if app is running
adb shell ps | Select-String "runanywhere"

# Force stop app
adb shell am force-stop com.runanywhere.startup_hackathon20

# Restart app
adb shell am start -n com.runanywhere.startup_hackathon20/.MainActivity
```

## Performance Optimization for Xiaomi

### Disable Battery Optimization

```powershell
# Via ADB
adb shell dumpsys deviceidle whitelist +com.runanywhere.startup_hackathon20
```

Or manually:

1. Settings → Apps → Startup Hackathon
2. Battery saver → No restrictions

### Enable All Permissions

1. Settings → Apps → Startup Hackathon
2. Permissions → Enable all

## Troubleshooting Checklist

- [ ] USB debugging enabled
- [ ] "Install via USB" enabled (if available)
- [ ] USB cable properly connected
- [ ] "File Transfer" mode selected
- [ ] ADB shows device as "device" (not "unauthorized")
- [ ] Old version uninstalled
- [ ] Gradle build successful
- [ ] APK installed successfully
- [ ] Permissions granted
- [ ] MIUI optimization disabled (if issues persist)
- [ ] Autostart enabled
- [ ] Battery restrictions removed

## Quick Commands Reference

```powershell
# Status check
adb devices
adb shell pm list packages | Select-String "runanywhere"

# Clean install
adb uninstall com.runanywhere.startup_hackathon20
.\gradlew clean assembleDebug installDebug

# Launch
adb shell am start -n com.runanywhere.startup_hackathon20/.MainActivity

# Debug
adb logcat | Select-String "runanywhere"

# Force stop
adb shell am force-stop com.runanywhere.startup_hackathon20
```

## Success Indicators

✅ App icon appears on Xiaomi home screen  
✅ App launches without crashes  
✅ Camera permission working  
✅ Location permission working  
✅ No MIUI security warnings  
✅ App runs smoothly

## Support

If issues persist:

1. Check `adb logcat` for error messages
2. Try disabling MIUI optimization
3. Ensure MIUI version is compatible (Android 7.0+)
4. Try restarting both phone and computer
5. Update MIUI to latest version

---

**Ready to install?** Run `.\reinstall.ps1` from project root!
