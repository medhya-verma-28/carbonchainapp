# Quick Run Guide - Carbon Registry App

## ✅ Device Connected

Your device is connected: **emulator-5554**

## 🚀 Steps to Run Latest App

### Method 1: Android Studio (RECOMMENDED)

1. **Open Android Studio**
2. **Open this project** if not already open:
    - File → Open → `C:/Users/medhy/StudioProjects/Hackss`

3. **Wait for Gradle Sync**
    - Look for "Gradle sync" message in bottom status bar
    - Wait until it says "Gradle sync finished"

4. **Uninstall old version** (optional but recommended):
    - In Android Studio top toolbar, click **Run** → **Stop**
    - Or manually uninstall from device/emulator

5. **Run the app**:
    - Click green **▶️ Run** button in toolbar
    - Or press `Shift + F10`
    - Select your device: **emulator-5554**
    - Click **OK**

6. **Android Studio will**:
    - ✅ Build the latest APK
    - ✅ Uninstall old version (if exists)
    - ✅ Install new version
    - ✅ Launch the app automatically

### Method 2: Using Gradle (Alternative)

If Android Studio is not working, you can build via command line:

```powershell
# Navigate to project
cd C:/Users/medhy/StudioProjects/Hackss

# Build debug APK (requires Java in PATH)
./gradlew assembleDebug

# Install on device
$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe install -r app/build/outputs/apk/debug/app-debug.apk
```

**Note**: This requires Java to be installed and in PATH.

## 🧪 Testing the App

Once launched, you should see:

### 1. **Login Screen** (First Screen)

- Modern dark theme with glass effect
- Two options: **User Login** | **Admin Login**

### 2A. **Test User Flow**

   ```
   Username: user
   Password: test123
   ```

- After login → **Blue Carbon Monitor** page
- Features:
    - 📸 Capture photo / Upload from gallery
    - 📍 Get GPS location
    - ⬆️ Upload to analysis
    - 📊 Collection status
    - 🚪 Logout (top right)

### 2B. **Test Admin Flow**

   ```
   Username: admin
   Password: admin123
   ```

- After login → **Carbon Registry Dashboard**
- Bottom navigation with 6 tabs:
    - Dashboard
    - Projects
    - Credits
    - Wallet
    - Profile
    - **Verification** (Admin only) ⭐

### 3. **Admin Verification Portal**

- Tap **Verification** tab (bottom right)
- See 3 submissions:
    - 2 PENDING (MANS-3821, MANS-3822)
    - 1 APPROVED (MANS-3820)
- Tap any submission to see details
- For PENDING items:
    - View image, location, metrics
    - Tap **Approve & Publish** (green button)
    - Or **Reject** (red button)

## 🎨 What to Look For

### Theme & Design:

- ✅ Dark blue-black background
- ✅ Green and blue gradient accents
- ✅ Frosted glass effect on cards
- ✅ Animated gradient backgrounds
- ✅ Smooth animations (60 FPS)

### Functionality:

- ✅ Login/Logout works
- ✅ Camera permission requested
- ✅ Location permission requested
- ✅ Photo capture works
- ✅ Gallery upload works
- ✅ GPS tracking works
- ✅ Admin verification loads
- ✅ Submissions show with images
- ✅ Approve/Reject dialogs work
- ✅ Navigation smooth

## ⚠️ Troubleshooting

### App won't install

```powershell
# Uninstall manually
$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe uninstall com.runanywhere.startup_hackathon20

# Then run from Android Studio again
```

### Gradle sync fails

1. In Android Studio: **File → Invalidate Caches / Restart**
2. Wait for restart
3. Let it sync again

### Build errors

1. **Build → Clean Project**
2. **Build → Rebuild Project**
3. Try running again

### Device not showing

1. Check USB cable connection
2. Enable USB debugging in phone settings
3. In Android Studio: **Run → Select Device**

### Permissions not working

- Grant permissions when app asks
- Or manually: Settings → Apps → Carbon Registry → Permissions

## 📱 Device Info

**Connected Device**: emulator-5554
**Project Path**: C:/Users/medhy/StudioProjects/Hackss
**Package Name**: com.runanywhere.startup_hackathon20

## ⏱️ Expected Build Time

- **First build**: 2-5 minutes (downloads dependencies)
- **Subsequent builds**: 30-60 seconds
- **Installation**: 5-10 seconds

---

## 🎯 Quick Start (TL;DR)

1. Open Android Studio
2. Open project: `C:/Users/medhy/StudioProjects/Hackss`
3. Wait for Gradle sync
4. Click **Run ▶️** (or press `Shift + F10`)
5. Select device: **emulator-5554**
6. App launches automatically

**Test Login:**

- User: `user` / `test123`
- Admin: `admin` / `admin123`

---

**Ready to go!** 🚀
