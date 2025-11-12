# Firebase Authentication Fix - Summary

## ✅ Problem Solved!

The Google Sign-In authentication was failing due to **missing Firebase dependencies** in
`build.gradle.kts`.

---

## 🔴 What Was Wrong

### Issue 1: Missing Firebase Libraries

The `FirebaseAuthService.kt` file was importing Firebase classes, but the dependencies were not
included in `build.gradle.kts`:

```kotlin
// These imports were failing:
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
```

**Error Output:**

```
> e: Unresolved reference 'FirebaseAuth'
> e: Unresolved reference 'FirebaseUser'
> e: Unresolved reference 'GoogleAuthProvider'
```

### Issue 2: Empty OAuth Client in google-services.json

Your `google-services.json` has an empty `oauth_client` array:

```json
"oauth_client": [],
```

This is because **SHA-1 certificate hasn't been added** to Firebase Console yet.

---

## ✅ What Was Fixed

### 1. Added Firebase Dependencies

Updated `app/build.gradle.kts` to include all required Firebase libraries:

```kotlin
// Firebase - explicit versions for compatibility
implementation("com.google.firebase:firebase-auth:23.1.0")
implementation("com.google.firebase:firebase-firestore:25.1.1")
implementation("com.google.firebase:firebase-analytics:22.1.2")
implementation("com.google.firebase:firebase-storage:21.0.1")
implementation("com.google.firebase:firebase-common-ktx:21.0.0")

// Coroutines support for Firebase
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")
```

### 2. Fixed FirebaseAuth Initialization

Changed from:

```kotlin
private val auth: FirebaseAuth = Firebase.auth  // ❌ Required -ktx import
```

To:

```kotlin
private val auth: FirebaseAuth = FirebaseAuth.getInstance()  // ✅ Works without -ktx
```

### 3. Updated Imports

Removed `-ktx` imports that weren't needed:

```kotlin
// REMOVED:
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

// These standard imports work fine:
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
```

### 4. Updated MainActivity.kt

Added handling for `UiState.Success` so authentication success messages are shown:

```kotlin
LaunchedEffect(uiState) {
    when (uiState) {
        is UiState.Success -> {  // ✅ NEW - Shows success messages
            snackbarHostState.showSnackbar((uiState as UiState.Success).message)
            viewModel.clearUiState()
        }
        is UiState.Error -> {
            snackbarHostState.showSnackbar((uiState as UiState.Error).message)
            viewModel.clearUiState()
        }
        else -> {}
    }
}
```

---

## 🚀 Build Status

✅ **BUILD SUCCESSFUL**

The project now compiles without errors:

```
> Task :app:compileDebugKotlin
BUILD SUCCESSFUL in 35s
19 actionable tasks: 19 executed
```

---

## ⚠️ What You Still Need To Do

The **code is fixed** and builds successfully, but Google Sign-In will still fail at runtime
because:

### 1. SHA-1 Certificate Not Added to Firebase

**Why it's needed:**

- Google uses SHA-1 to verify your app's identity
- Without it, Firebase doesn't generate OAuth credentials
- Result: `oauth_client` array is empty in `google-services.json`

**How to fix:**

```powershell
# Run this to get your SHA-1:
./gradlew signingReport

# Or use the automated script:
./setup-firebase.ps1
```

Then add the SHA-1 to Firebase Console:

1. Go to https://console.firebase.google.com/
2. Select project: **carbonchainapp**
3. Settings → Your apps → Add fingerprint
4. Paste SHA-1 → Save

### 2. Download Updated google-services.json

After adding SHA-1:

1. Go to Firebase Console → Project settings
2. Download new `google-services.json`
3. Replace `app/google-services.json`

The new file will have OAuth credentials:

```json
"oauth_client": [
    {
        "client_id": "441252740217-xxxxx.apps.googleusercontent.com",
        "client_type": 3
    }
]
```

### 3. Update strings.xml

Extract the Web Client ID from the new `google-services.json`:

1. Find `client_id` with `"client_type": 3`
2. Copy the client ID
3. Update `app/src/main/res/values/strings.xml`:

```xml
<string name="default_web_client_id">PASTE_CLIENT_ID_HERE</string>
```

### 4. Enable Google Sign-In in Firebase

1. Firebase Console → Authentication → Sign-in method
2. Enable Google provider
3. Enter support email
4. Save

### 5. Rebuild and Install

```powershell
./gradlew clean build
adb uninstall com.runanywhere.startup_hackathon20
./gradlew installDebug
```

---

## 📋 Quick Checklist

**Code Fixes (DONE):**

- [x] Firebase dependencies added to build.gradle.kts
- [x] FirebaseAuthService.kt imports fixed
- [x] FirebaseAuth initialization updated
- [x] MainActivity.kt success state handling added
- [x] Project builds successfully

**Firebase Configuration (TODO):**

- [ ] Get SHA-1 certificate (`./gradlew signingReport`)
- [ ] Add SHA-1 to Firebase Console
- [ ] Enable Google Sign-In in Firebase Authentication
- [ ] Download updated google-services.json
- [ ] Replace app/google-services.json
- [ ] Update strings.xml with Web Client ID
- [ ] Rebuild and reinstall app

---

## 🎯 Testing After Setup

Once you complete the Firebase configuration:

1. **Open app** → Login screen
2. **Select login type** (User or Admin)
3. **Click "Sign in with Google"**
4. **Select Google account**
5. **See success message**: "Login successful! Welcome [Name]"
6. **Navigate to**: Blue Carbon Monitor (User) or Carbon Registry (Admin)

---

## 📁 Helper Scripts Created

1. **`setup-firebase.ps1`** - Automated setup script
    - Gets SHA-1 automatically
    - Opens Firebase Console
    - Guides through setup
    - Updates strings.xml automatically

2. **`get-sha1.ps1`** - Just gets SHA-1 certificate

3. **`FIX_GOOGLE_AUTH.md`** - Detailed manual instructions

---

## 🎉 Summary

**FIXED:**

- ✅ Compilation errors in FirebaseAuthService.kt
- ✅ Missing Firebase dependencies
- ✅ Build now succeeds
- ✅ UI success state handling

**REMAINING:**

- ⏳ Add SHA-1 to Firebase (5 minutes)
- ⏳ Download new google-services.json (2 minutes)
- ⏳ Update strings.xml (1 minute)
- ⏳ Enable Google Sign-In (1 minute)
- ⏳ Rebuild and test (2 minutes)

**Total time to complete: ~10 minutes**

---

## 🚀 Next Step

Run the automated setup:

```powershell
./setup-firebase.ps1
```

Or follow the manual steps in `FIX_GOOGLE_AUTH.md`.

After completing the Firebase configuration, Google Sign-In will work perfectly! 🎊
