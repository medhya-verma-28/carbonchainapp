# Carbon Registry App - Quick Run Script
# This script builds and installs the app on your connected Android device

Write-Host "🌱 Carbon Registry - Quick Run Script" -ForegroundColor Green
Write-Host "======================================" -ForegroundColor Green
Write-Host ""

# Set project directory
$ProjectDir = "C:\Users\medhy\StudioProjects\Hackss"
$AndroidSdk = "C:\Users\medhy\AppData\Local\Android\Sdk"
$AdbPath = "$AndroidSdk\platform-tools\adb.exe"

# Check if project exists
if (-not (Test-Path $ProjectDir)) {
    Write-Host "❌ Error: Project directory not found!" -ForegroundColor Red
    Write-Host "   Expected: $ProjectDir" -ForegroundColor Yellow
    exit 1
}

# Navigate to project
Write-Host "📂 Navigating to project directory..." -ForegroundColor Cyan
Set-Location $ProjectDir

# Check if ADB exists
if (-not (Test-Path $AdbPath)) {
    Write-Host "❌ Error: ADB not found!" -ForegroundColor Red
    Write-Host "   Expected: $AdbPath" -ForegroundColor Yellow
    Write-Host "   Please install Android SDK or update the path in this script" -ForegroundColor Yellow
    exit 1
}

# Check connected devices
Write-Host "📱 Checking connected devices..." -ForegroundColor Cyan
$devices = & $AdbPath devices
Write-Host $devices

if ($devices -match "device$") {
    Write-Host "✅ Device connected!" -ForegroundColor Green
} else {
    Write-Host "❌ No device detected!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please:" -ForegroundColor Yellow
    Write-Host "  1. Connect your phone via USB" -ForegroundColor Yellow
    Write-Host "  2. Enable USB debugging in Developer Options" -ForegroundColor Yellow
    Write-Host "  3. Accept 'Allow USB debugging' prompt on phone" -ForegroundColor Yellow
    Write-Host "  4. Run this script again" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Or use Android Studio instead (see RUN_APP.md)" -ForegroundColor Cyan
    exit 1
}

Write-Host ""
Write-Host "🔨 Building APK..." -ForegroundColor Cyan
Write-Host "   This may take 1-3 minutes on first build..." -ForegroundColor Yellow
Write-Host ""

# Set JAVA_HOME
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Build the APK
try {
    & .\gradlew.bat assembleDebug
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Build failed!" -ForegroundColor Red
        Write-Host "   Please check the errors above" -ForegroundColor Yellow
        Write-Host "   Or use Android Studio (easier): see RUN_APP.md" -ForegroundColor Cyan
        exit 1
    }
} catch {
    Write-Host "❌ Build failed: $_" -ForegroundColor Red
    Write-Host "   Please use Android Studio instead (see RUN_APP.md)" -ForegroundColor Cyan
    exit 1
}

Write-Host ""
Write-Host "✅ Build successful!" -ForegroundColor Green
Write-Host ""

# Check if APK exists
$ApkPath = "$ProjectDir\app\build\outputs\apk\debug\app-debug.apk"
if (-not (Test-Path $ApkPath)) {
    Write-Host "❌ APK not found at expected location!" -ForegroundColor Red
    Write-Host "   Expected: $ApkPath" -ForegroundColor Yellow
    exit 1
}

# Install APK
Write-Host "📲 Installing app on device..." -ForegroundColor Cyan
& $AdbPath install -r $ApkPath

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ App installed successfully!" -ForegroundColor Green
    Write-Host ""
    
    # Launch the app
    Write-Host "🚀 Launching app..." -ForegroundColor Cyan
    & $AdbPath shell am start -n com.runanywhere.startup_hackathon20/.MainActivity
    
    Write-Host ""
    Write-Host "✨ Done! The app should now be running on your device." -ForegroundColor Green
    Write-Host ""
    Write-Host "Test Credentials:" -ForegroundColor Cyan
    Write-Host "  👤 User Login:  user / test123" -ForegroundColor White
    Write-Host "  ⚙️  Admin Login: admin / admin123" -ForegroundColor White
    Write-Host ""
    Write-Host "📖 For more info, see RUN_APP.md" -ForegroundColor Cyan
} else {
    Write-Host "❌ Installation failed!" -ForegroundColor Red
    Write-Host "   Try uninstalling the old version first:" -ForegroundColor Yellow
    Write-Host "   $AdbPath uninstall com.runanywhere.startup_hackathon20" -ForegroundColor Yellow
    exit 1
}
