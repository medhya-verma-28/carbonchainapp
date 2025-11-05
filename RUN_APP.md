# 🚀 Run Carbon Registry App on Your Phone

## 📋 Prerequisites Check

Before running the app, ensure you have:

- ✅ **Android Studio** installed
- ✅ **Java JDK 17** installed
- ✅ **Android phone** or **emulator** ready
- ✅ **USB debugging** enabled on phone (if using physical device)

## 🎯 Quick Start (EASIEST METHOD)

### Option A: Using Android Studio (Recommended)

1. **Open Android Studio**
    - Launch Android Studio
    - Click "Open an existing project"
    - Navigate to: `C:\Users\medhy\StudioProjects\Hackss`

2. **Wait for Gradle Sync**
    - Bottom status bar will show "Gradle sync in progress..."
    - Wait until it shows "Gradle sync finished" (1-3 minutes)
    - If errors occur, click "Try Again" or "Sync Now"

3. **Connect Your Device**

   **For Physical Phone:**
    - Connect via USB cable
    - Enable USB debugging in phone settings:
        - Settings → About Phone → Tap "Build Number" 7 times
        - Settings → Developer Options → Enable "USB Debugging"
    - Accept debugging prompt on phone

   **For Emulator:**
    - In Android Studio, click AVD Manager (phone icon in toolbar)
    - Create/Start an Android Virtual Device
    - Or use existing emulator-5554

4. **Run the App**
    - Click the green **▶️ Run** button in toolbar
    - OR press `Shift + F10`
    - Select your device from dropdown
    - Click **OK**

5. **Wait for Installation**
    - Android Studio will:
        - Build the APK (30-60 seconds)
        - Install on device (5-10 seconds)
        - Launch the app automatically

---

## 🔧 Option B: Using Command Line

### Step 1: Setup Environment

```powershell
# Open PowerShell as Administrator
# Set JAVA_HOME (adjust path if needed)
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Verify Java
java -version
```

### Step 2: Build the App

```powershell
# Navigate to project
cd C:\Users\medhy\StudioProjects\Hackss

# Clean and build
.\gradlew clean assembleDebug
```

### Step 3: Install on Phone

```powershell
# Using full path to ADB
C:\Users\medhy\AppData\Local\Android\Sdk\platform-tools\adb.exe devices

# Install APK
C:\Users\medhy\AppData\Local\Android\Sdk\platform-tools\adb.exe install -r app\build\outputs\apk\debug\app-debug.apk

# Launch app
C:\Users\medhy\AppData\Local\Android\Sdk\platform-tools\adb.exe shell am start -n com.runanywhere.startup_hackathon20/.MainActivity
```

---

## 🧪 Testing the Latest App

### 🔐 Login Screen (First)

When you launch the app, you'll see a beautiful dark blue gradient login screen with glass effect.

**Choose login type:**

- 👤 **User Login** - For Blue Carbon Monitor
- ⚙️ **Admin Login** - For Carbon Registry Portal

---

### 👤 USER LOGIN - Blue Carbon Monitor

**Test Credentials:**

```
Username: user
Password: test123
```

**Or Register New Account:**

- Click "Don't have an account? Register"
- Enter username, email, password
- Click "Register"

**Features:**

1. **Photo Documentation**
    - 📸 Capture photo with camera
    - 🖼️ Upload from gallery
    - Preview captured image
    - Remove and retake if needed

2. **Location Tracking**
    - 📍 Click "Update Location"
    - Grant location permission
    - GPS coordinates displayed

3. **Upload to Analysis**
    - ⬆️ Click "Upload to Analysis" (green button)
    - Submission sent to admin for verification
    - Success message appears

4. **Collection Status**
    - ✅ Green dot = completed
    - ⚪ Gray dot = pending
    - Track GPS and photo status

5. **Logout**
    - 🚪 Click logout icon (top right)

---

### ⚙️ ADMIN LOGIN - Carbon Registry Portal

**Test Credentials:**

```
Username: admin
Password: admin123
```

**Features:**

#### 1. 📊 Dashboard Tab

- Global impact statistics
- Featured carbon projects
- Recent carbon credits
- Glass effect cards with animations

#### 2. 🌱 Projects Tab

- Browse 5 carbon offset projects:
    - Amazon Rainforest Conservation (Brazil)
    - Solar Farm India
    - Wind Energy Denmark
    - Mangrove Restoration Vietnam
    - Methane Capture Dairy Farm (USA)
- Search and filter by project type
- View project details

#### 3. 💳 Credits Tab

- View all carbon credits
- Filter by status: Active, Retired, Pending, etc.
- Buy credits
- Retire credits
- Verify on blockchain

#### 4. 💼 Wallet Tab

- View wallet address
- Credits owned and retired
- Transaction history
- Blockchain verification

#### 5. 👤 Profile Tab

- User information
- Email and role badge
- Wallet summary
- Logout option

#### 6. ✅ Verification Tab (ADMIN ONLY) ⭐

**This is the NEW feature!**

- View all user submissions
- 3 sample submissions:
    - ✅ MANS-3820 - APPROVED
    - ⏳ MANS-3821 - PENDING
    - ⏳ MANS-3822 - PENDING

**For PENDING submissions:**

- Tap to view details
- See submitted photo
- View GPS location and metrics
- Click **"Approve & Publish"** (green) or **"Reject"** (red)
- Confirmation dialog appears
- Status updates to APPROVED/REJECTED

---

## 🎨 What You Should See

### Visual Design:

- ✅ Dark blue-black gradient background
- ✅ Green and teal accent colors
- ✅ Frosted glass effect on all cards
- ✅ Smooth animations (fade, slide)
- ✅ Modern Material 3 design
- ✅ Animated gradient blobs on login
- ✅ Professional UI/UX

### Performance:

- ✅ 60 FPS smooth scrolling
- ✅ Fast navigation between tabs
- ✅ Instant button responses
- ✅ Loading indicators where needed
- ✅ Success/error messages as snackbars

---

## ⚠️ Troubleshooting

### Problem: "Device not detected"

**Solution:**

```powershell
# Check device connection
C:\Users\medhy\AppData\Local\Android\Sdk\platform-tools\adb.exe devices

# If empty list:
# 1. Check USB cable
# 2. Enable USB debugging on phone
# 3. Accept "Allow USB debugging?" on phone
# 4. Try different USB port

# Restart ADB server
C:\Users\medhy\AppData\Local\Android\Sdk\platform-tools\adb.exe kill-server
C:\Users\medhy\AppData\Local\Android\Sdk\platform-tools\adb.exe start-server
```

### Problem: "Gradle sync failed"

**Solution:**

1. In Android Studio: **File → Invalidate Caches → Invalidate and Restart**
2. Wait for restart
3. Click "Sync Now" or "Try Again"

### Problem: "Build failed" or errors

**Solution:**

```powershell
# In Android Studio:
# 1. Build → Clean Project
# 2. Build → Rebuild Project

# Or via command line:
cd C:\Users\medhy\StudioProjects\Hackss
.\gradlew clean build
```

### Problem: "App crashes on launch"

**Solution:**

```powershell
# View crash logs
C:\Users\medhy\AppData\Local\Android\Sdk\platform-tools\adb.exe logcat | findstr "AndroidRuntime"

# Or in Android Studio:
# View → Tool Windows → Logcat
```

### Problem: "Permission denied" errors

**Solution:**

- Grant permissions when app prompts
- Or manually: Phone Settings → Apps → Carbon Registry → Permissions
- Enable: Camera, Location, Storage

### Problem: "Java not found"

**Solution:**

```powershell
# Set JAVA_HOME
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Verify
java -version
```

---

## 📝 Test Checklist

### User Flow ✅

- [ ] Login screen loads with glass effect
- [ ] Can register new user account
- [ ] Can login as existing user
- [ ] Blue Carbon Monitor page loads
- [ ] Camera permission requested
- [ ] Can capture photo with camera
- [ ] Can upload from gallery
- [ ] Photo preview shows correctly
- [ ] Location permission requested
- [ ] GPS location updates
- [ ] Upload button enables when photo added
- [ ] Upload shows success message
- [ ] Collection status updates
- [ ] Can logout

### Admin Flow ✅

- [ ] Can login as admin
- [ ] Dashboard shows statistics
- [ ] Projects tab loads 5 projects
- [ ] Can search and filter projects
- [ ] Credits tab shows credits
- [ ] Wallet shows address and balance
- [ ] Profile shows admin badge
- [ ] **Verification tab appears (Admin only)**
- [ ] 3 submissions visible
- [ ] Can tap submission to view details
- [ ] Photo loads in detail view
- [ ] GPS coordinates shown
- [ ] Approve button works
- [ ] Reject button works
- [ ] Status updates after action
- [ ] Can logout

---

## 📱 Device Information

**Your Setup:**

- **Project Path:** `C:\Users\medhy\StudioProjects\Hackss`
- **Package Name:** `com.runanywhere.startup_hackathon20`
- **Android SDK:** `C:\Users\medhy\AppData\Local\Android\Sdk`
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 36 (Android 14)

---

## 🚀 One-Click Run (Easiest!)

**Just use Android Studio:**

1. Open Android Studio
2. Open this project
3. Click **Run ▶️**
4. Done! 🎉

**First build takes 2-3 minutes. Subsequent builds: 30 seconds.**

---

## 🆘 Need Help?

If you encounter any issues:

1. Check this guide's Troubleshooting section
2. View Android Studio's Build output window
3. Check Logcat for runtime errors
4. Ensure all permissions granted on phone

---

## ✨ Latest Features (Just Added)

1. **Admin Verification Portal** - Review and approve user submissions
2. **Beautiful glass effect UI** - Frosted glass cards throughout
3. **Animated gradients** - Smooth color transitions
4. **Enhanced login flow** - User registration + admin login
5. **Photo documentation** - Camera + Gallery support
6. **GPS tracking** - Real-time location updates
7. **Status indicators** - Visual collection status
8. **Role-based access** - Different views for User vs Admin

---

**You're all set! 🌱 Enjoy the Carbon Registry App!**
