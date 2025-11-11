# Firebase Installation Guide for Blue Carbon Monitor

## ✅ Status: Ready to Install

All Firebase code is already implemented in your project. You just need to complete the Firebase
Console setup and sync.

---

## 📋 Current Project Status

### ✅ Already Implemented:

- Firebase dependencies in `build.gradle.kts`
- Google Services plugin configured
- `FirebaseAuthService.kt` with complete auth logic
- `CarbonViewModel.kt` integrated with Firebase
- Login screen with Email/Password and Google Sign-In buttons
- `google-services.json` template ready

### ⏳ Needs Setup:

- Firebase Console configuration
- Download actual `google-services.json` file
- Enable Authentication in Firebase Console
- Sync Gradle to download dependencies

---

## 🚀 Installation Steps (10 Minutes)

### Step 1: Firebase Console Setup (5 min)

#### 1.1 - Go to Firebase Console

1. Open: https://console.firebase.google.com/
2. You should see your project: **carbonchainplus**
3. Click on it to open

#### 1.2 - Add Android App (if not already added)

1. Click the gear icon ⚙️ → **Project settings**
2. Scroll to **"Your apps"** section
3. If you don't see an Android app:
    - Click **"Add app"** → Select **Android** icon
    - **Android package name**: `com.runanywhere.startup_hackathon20`
    - **App nickname** (optional): `Blue Carbon Monitor`
    - **Debug signing certificate SHA-1** (optional for now)
    - Click **"Register app"**

#### 1.3 - Download google-services.json

1. In the setup wizard or Project Settings
2. Click **"Download google-services.json"**
3. Save it somewhere safe
4. **IMPORTANT**: Replace the file at:
   ```
   C:/Users/medhy/StudioProjects/Hackss/app/google-services.json
   ```
   with the downloaded file

#### 1.4 - Enable Email/Password Authentication

1. In Firebase Console left menu, click **"Authentication"**
2. Click **"Get started"** (if first time)
3. Go to **"Sign-in method"** tab
4. Click on **"Email/Password"** provider
5. **Toggle Enable** for "Email/Password"
6. Click **"Save"**

#### 1.5 - Enable Google Sign-In

1. Still in **"Sign-in method"** tab
2. Click on **"Google"** provider
3. **Toggle Enable**
4. **Project public-facing name**: `Blue Carbon Monitor`
5. **Project support email**: Select your email from dropdown
6. Click **"Save"**

#### 1.6 - Get Web Client ID (for Google Sign-In)

1. In **"Sign-in method"** tab, expand **"Google"** provider
2. You'll see **"Web SDK configuration"**
3. Copy the **Web client ID** (looks like: `123456789-abc...xyz.apps.googleusercontent.com`)
4. Keep this for the next step

---

### Step 2: Update Project Configuration (2 min)

#### 2.1 - Update strings.xml

Open: `app/src/main/res/values/strings.xml`

Replace this line:

```xml
<string name="default_web_client_id">YOUR_WEB_CLIENT_ID_HERE</string>
```

With your actual Web Client ID:

```xml
<string name="default_web_client_id">PASTE_YOUR_WEB_CLIENT_ID_FROM_STEP_1.6_HERE</string>
```

#### 2.2 - Verify google-services.json

Ensure the file at `app/google-services.json` is the one you downloaded from Firebase Console (not
the template).

---

### Step 3: Sync and Build (3 min)

#### Option A: Using Android Studio (Recommended)

1. **Open Android Studio**
2. Open your project: `C:/Users/medhy/StudioProjects/Hackss`
3. **Sync Gradle**:
    - Click: `File` → `Sync Project with Gradle Files`
    - OR: Click the sync icon (🔄) in the toolbar
4. **Wait for sync** to complete (downloads Firebase SDKs ~10-15 MB)
5. **Rebuild Project**:
    - Click: `Build` → `Rebuild Project`
6. **Check for errors** in Build output

#### Option B: Using Command Line

```powershell
# Navigate to project directory
cd C:/Users/medhy/StudioProjects/Hackss

# Clean and build
./gradlew clean build

# If successful, install on device
./gradlew installDebug
```

---

## 🧪 Testing Your Firebase Installation

### Test 1: Check Dependencies Downloaded

After Gradle sync, check if Firebase dependencies were downloaded:

```powershell
./gradlew app:dependencies | findstr firebase
```

You should see:

```
+--- com.google.firebase:firebase-auth-ktx
+--- com.google.firebase:firebase-firestore-ktx
+--- com.google.firebase:firebase-analytics-ktx
+--- com.google.firebase:firebase-storage-ktx
```

### Test 2: Build the Project

```powershell
./gradlew assembleDebug
```

Expected output:

```
BUILD SUCCESSFUL in 2m 30s
```

### Test 3: Test Authentication

1. **Run the app** on your device/emulator
2. **Test Email Registration**:
    - Select "User Login"
    - Click "Don't have an account? Register"
    - Enter: username, email, password
    - Click "Register"
    - ✅ Should create account in Firebase

3. **Test Email Login**:
    - Enter username and password
    - Click "Login"
    - ✅ Should log in successfully

4. **Test Google Sign-In**:
    - Click "Sign in with Google"
    - Choose Google account
    - ✅ Should authenticate and log in

5. **Verify in Firebase Console**:
    - Go to Firebase Console → Authentication → Users
    - You should see your test accounts listed

---

## 📱 Project Structure

### Firebase Files in Your Project:

```
Hackss/
├── build.gradle.kts                      ✅ Google Services plugin
├── app/
│   ├── build.gradle.kts                  ✅ Firebase dependencies
│   ├── google-services.json              ⚠️ Replace with actual file
│   ├── src/main/
│   │   ├── AndroidManifest.xml           ✅ No changes needed
│   │   ├── res/values/strings.xml        ⚠️ Add Web Client ID
│   │   └── java/.../
│   │       ├── firebase/
│   │       │   └── FirebaseAuthService.kt ✅ Already implemented
│   │       ├── viewmodel/
│   │       │   └── CarbonViewModel.kt     ✅ Already integrated
│   │       └── MainActivity.kt            ✅ UI already has buttons
```

---

## 🔍 Verification Checklist

Before testing, verify:

- [ ] Firebase project "carbonchainplus" exists
- [ ] Android app registered in Firebase Console
- [ ] `google-services.json` downloaded and placed in `app/` folder
- [ ] Email/Password authentication enabled in Firebase
- [ ] Google Sign-In enabled in Firebase
- [ ] Web Client ID copied to `strings.xml`
- [ ] Gradle sync completed successfully
- [ ] Project builds without errors

---

## ⚙️ Firebase Configuration Summary

### Your Firebase Project:

- **Project ID**: carbonchainplus
- **Project Number**: 551902001389
- **API Key**: AIzaSyBJK9YfQUeiYuYsqhiffmx3VWIRbxt4VKs
- **Storage Bucket**: carbonchainplus.firebasestorage.app
- **Package Name**: com.runanywhere.startup_hackathon20

### Authentication Methods Enabled:

- ✅ Email/Password
- ✅ Google Sign-In

### Dependencies Included:

```gradle
implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
implementation("com.google.firebase:firebase-auth-ktx")
implementation("com.google.firebase:firebase-firestore-ktx")
implementation("com.google.firebase:firebase-analytics-ktx")
implementation("com.google.firebase:firebase-storage-ktx")
implementation("com.google.android.gms:play-services-auth:20.7.0")
```

---

## 🐛 Troubleshooting

### Error: "google-services plugin could not detect"

**Solution**: Ensure `google-services.json` is in the `app/` directory, not project root.

```powershell
# Verify file location
Test-Path "C:/Users/medhy/StudioProjects/Hackss/app/google-services.json"
# Should return: True
```

### Error: "Default FirebaseApp is not initialized"

**Solution**:

1. Verify `google-services.json` is the actual file from Firebase Console
2. Clean and rebuild project
3. Uninstall app from device and reinstall

### Error: "API key not valid"

**Solution**: Download fresh `google-services.json` from Firebase Console

### Error: "DEVELOPER_ERROR" during Google Sign-In

**Solution**:

1. Verify Web Client ID in `strings.xml` is correct
2. Check package name matches exactly: `com.runanywhere.startup_hackathon20`

### Build Error: "Unresolved reference 'FirebaseAuth'"

**Solution**: Gradle sync needed

```powershell
./gradlew clean build
```

---

## 📞 Need Help?

### Check Firebase Console Status:

1. Go to Firebase Console
2. Click "Usage" → "Status"
3. Ensure all services are green

### View Authentication Logs:

1. Firebase Console → Authentication → Users
2. Check if test users appear after registration

### Debug Logs:

```kotlin
// Already added in FirebaseAuthService.kt
// Logs appear in Android Studio Logcat
```

---

## 🎯 Quick Command Reference

```powershell
# Navigate to project
cd C:/Users/medhy/StudioProjects/Hackss

# Clean build
./gradlew clean

# Build project
./gradlew build

# Install on device
./gradlew installDebug

# Run app
./gradlew installDebug
adb shell am start -n com.runanywhere.startup_hackathon20/.MainActivity

# Check dependencies
./gradlew app:dependencies | findstr firebase

# View build errors
./gradlew build --info

# Force dependency refresh
./gradlew build --refresh-dependencies
```

---

## ✅ After Successful Installation

You will have:

1. ✅ **Firebase Authentication** - Users can register and login
2. ✅ **Google Sign-In** - One-tap Google authentication
3. ✅ **User Management** - All user data stored in Firebase
4. ✅ **Analytics** - Track user engagement automatically
5. ✅ **Cloud Storage** - Ready for profile photos and documents
6. ✅ **Firestore** - Ready for storing carbon credit data

---

## 🚀 Next Steps After Installation

1. **Test all authentication flows**
2. **Add test users in Firebase Console**
3. **Set up Firestore database** (for storing carbon credits)
4. **Configure Firebase Storage** (for photo documentation)
5. **Set up Firebase Analytics** (track user behavior)
6. **Deploy to production**

---

## 📊 Firebase Console Quick Links

- **Console**: https://console.firebase.google.com/project/carbonchainplus
- **Authentication**: https://console.firebase.google.com/project/carbonchainplus/authentication
- **Firestore**: https://console.firebase.google.com/project/carbonchainplus/firestore
- **Storage**: https://console.firebase.google.com/project/carbonchainplus/storage
- **Analytics**: https://console.firebase.google.com/project/carbonchainplus/analytics

---

## 🎉 Installation Complete!

Once you complete these steps, your Firebase installation will be complete and functional!

**Time Required**: ~10 minutes
**Difficulty**: Easy
**Result**: Production-ready authentication system

---

**Questions?** Check the troubleshooting section or the detailed guides:

- `FIREBASE_INTEGRATION.md`
- `GOOGLE_SIGN_IN_SETUP_COMPLETE.md`
- `AUTHENTICATION_COMPLETE_SUMMARY.md`
