#!/usr/bin/env pwsh
# Xiaomi App Installation Script
# Run this from project root: .\install-xiaomi.ps1

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "   Xiaomi App Reinstall & Run Script" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# Configuration
$PACKAGE_NAME = "com.runanywhere.startup_hackathon20"
$MAIN_ACTIVITY = "$PACKAGE_NAME/.MainActivity"

# Function to check command exists
function Test-Command {
    param($Command)
    try {
        if (Get-Command $Command -ErrorAction Stop) {
            return $true
        }
    }
    catch {
        return $false
    }
}

# Step 1: Check prerequisites
Write-Host "Step 1: Checking prerequisites..." -ForegroundColor Yellow

if (-not (Test-Command "adb")) {
    Write-Host "❌ ADB not found! Please install Android SDK Platform Tools." -ForegroundColor Red
    Write-Host "Download from: https://developer.android.com/studio/releases/platform-tools" -ForegroundColor Yellow
    exit 1
}
Write-Host "✓ ADB found" -ForegroundColor Green

if (-not (Test-Path ".\gradlew.bat")) {
    Write-Host "❌ gradlew.bat not found! Are you in the project root?" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Gradle wrapper found" -ForegroundColor Green

# Step 2: Check device connection
Write-Host "`nStep 2: Checking device connection..." -ForegroundColor Yellow

$devices = adb devices | Select-Object -Skip 1 | Where-Object { $_ -match "device$" }
if (-not $devices) {
    Write-Host "❌ No device connected!" -ForegroundColor Red
    Write-Host "`nPlease ensure:" -ForegroundColor Yellow
    Write-Host "  1. USB debugging is enabled on Xiaomi phone" -ForegroundColor White
    Write-Host "  2. Phone is connected via USB" -ForegroundColor White
    Write-Host "  3. File Transfer mode is selected" -ForegroundColor White
    Write-Host "  4. USB debugging prompt is accepted" -ForegroundColor White
    Write-Host "`nRun 'adb devices' to check connection" -ForegroundColor Yellow
    exit 1
}
Write-Host "✓ Device connected: $devices" -ForegroundColor Green

# Step 3: Uninstall old version
Write-Host "`nStep 3: Uninstalling old version..." -ForegroundColor Yellow

$uninstallResult = adb uninstall $PACKAGE_NAME 2>&1
if ($uninstallResult -match "Success") {
    Write-Host "✓ Old version uninstalled" -ForegroundColor Green
} elseif ($uninstallResult -match "Unknown package") {
    Write-Host "ℹ No previous installation found (fresh install)" -ForegroundColor Cyan
} else {
    Write-Host "⚠ Uninstall result: $uninstallResult" -ForegroundColor Yellow
}

# Clear MIUI package installer cache
Write-Host "  Clearing MIUI package installer cache..." -ForegroundColor Gray
adb shell pm clear com.miui.packageinstaller 2>&1 | Out-Null

# Step 4: Clean project
Write-Host "`nStep 4: Cleaning project..." -ForegroundColor Yellow

.\gradlew.bat clean | Out-Null
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Project cleaned" -ForegroundColor Green
} else {
    Write-Host "❌ Clean failed!" -ForegroundColor Red
    exit 1
}

# Step 5: Build APK
Write-Host "`nStep 5: Building APK..." -ForegroundColor Yellow
Write-Host "  This may take a few minutes..." -ForegroundColor Gray

.\gradlew.bat assembleDebug
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ APK built successfully" -ForegroundColor Green
} else {
    Write-Host "❌ Build failed! Check error messages above." -ForegroundColor Red
    exit 1
}

# Step 6: Install APK
Write-Host "`nStep 6: Installing APK on Xiaomi device..." -ForegroundColor Yellow

.\gradlew.bat installDebug
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ APK installed successfully" -ForegroundColor Green
} else {
    Write-Host "❌ Installation failed!" -ForegroundColor Red
    Write-Host "`nTroubleshooting:" -ForegroundColor Yellow
    Write-Host "  1. Check if 'Install via USB' is enabled in Developer options" -ForegroundColor White
    Write-Host "  2. Try disabling MIUI optimization in Developer options" -ForegroundColor White
    Write-Host "  3. Check Settings → Privacy → Install from external sources" -ForegroundColor White
    exit 1
}

# Step 7: Verify installation
Write-Host "`nStep 7: Verifying installation..." -ForegroundColor Yellow

$installed = adb shell pm list packages | Select-String $PACKAGE_NAME
if ($installed) {
    Write-Host "✓ App verified installed: $installed" -ForegroundColor Green
} else {
    Write-Host "⚠ Could not verify installation" -ForegroundColor Yellow
}

# Step 8: Grant permissions
Write-Host "`nStep 8: Granting permissions..." -ForegroundColor Yellow

$permissions = @(
    "android.permission.CAMERA",
    "android.permission.ACCESS_FINE_LOCATION",
    "android.permission.ACCESS_COARSE_LOCATION",
    "android.permission.READ_EXTERNAL_STORAGE",
    "android.permission.WRITE_EXTERNAL_STORAGE",
    "android.permission.READ_MEDIA_IMAGES"
)

$grantedCount = 0
foreach ($permission in $permissions) {
    $result = adb shell pm grant $PACKAGE_NAME $permission 2>&1
    if ($result -notmatch "error" -and $result -notmatch "failed") {
        $grantedCount++
        Write-Host "  ✓ Granted: $permission" -ForegroundColor Gray
    }
}
Write-Host "✓ Granted $grantedCount/$($permissions.Count) permissions" -ForegroundColor Green

# Step 9: Disable battery optimization
Write-Host "`nStep 9: Optimizing app settings..." -ForegroundColor Yellow

adb shell dumpsys deviceidle whitelist +$PACKAGE_NAME 2>&1 | Out-Null
Write-Host "  ✓ Battery optimization disabled" -ForegroundColor Gray

# Step 10: Launch app
Write-Host "`nStep 10: Launching app..." -ForegroundColor Yellow

Start-Sleep -Seconds 1
$launchResult = adb shell am start -n $MAIN_ACTIVITY 2>&1

if ($launchResult -match "Starting") {
    Write-Host "✓ App launched successfully!" -ForegroundColor Green
} else {
    Write-Host "⚠ Launch result: $launchResult" -ForegroundColor Yellow
}

# Final summary
Write-Host "`n============================================" -ForegroundColor Cyan
Write-Host "         Installation Complete! ✓" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Next Steps:" -ForegroundColor Yellow
Write-Host "  1. Check your Xiaomi phone - app should be running" -ForegroundColor White
Write-Host "  2. Grant any additional permissions if prompted" -ForegroundColor White
Write-Host "  3. If permissions issues, manually enable in Settings → Apps" -ForegroundColor White
Write-Host ""
Write-Host "Debugging:" -ForegroundColor Yellow
Write-Host "  • View logs: adb logcat | Select-String 'runanywhere'" -ForegroundColor Gray
Write-Host "  • Force stop: adb shell am force-stop $PACKAGE_NAME" -ForegroundColor Gray
Write-Host "  • Relaunch: adb shell am start -n $MAIN_ACTIVITY" -ForegroundColor Gray
Write-Host ""

# Ask if user wants to see logs
$viewLogs = Read-Host "Do you want to view app logs? (y/n)"
if ($viewLogs -eq "y" -or $viewLogs -eq "Y") {
    Write-Host "`nShowing app logs (Ctrl+C to stop)..." -ForegroundColor Yellow
    Write-Host "================================================`n" -ForegroundColor Cyan
    adb logcat | Select-String "runanywhere"
}
