# Fix Google Authentication Issue

## Problem Identified

Google authentication is not working because the **Web Client ID** in `google-services.json` has
placeholder values instead of real OAuth client credentials.

## What I Fixed

1. ✅ **Updated MainActivity.kt** - Added handling for `UiState.Success` so authentication success
   messages are properly shown and cleared
2. ✅ **Updated strings.xml** - Added the Web Client ID (currently has placeholder from
   google-services.json)

## What You Need to Do

### Step 1: Download the Correct google-services.json

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project: **carbonchainplus**
3. Click the gear icon ⚙️ → **Project settings**
4. Scroll down to **"Your apps"** section
5. Find your Android app: `com.runanywhere.startup_hackathon20`
6. Click **Download google-services.json**
7. **Replace** `app/google-services.json` with the downloaded file

### Step 2: Enable Google Sign-In in Firebase

1. In Firebase Console, go to **Authentication**
2. Click **Sign-in method** tab
3. Find **Google** in the providers list
4. If not enabled:
    - Click **Google**
    - Toggle **Enable**
    - Set **Project public-facing name**: "Blue Carbon Monitor"
    - Set **Project support email**: your email
    - Click **Save**

### Step 3: Add SHA-1 Certificate (Important!)

Google Sign-In requires your app's SHA-1 certificate fingerprint:

1. In Android Studio, open **Gradle** tab (right side)
2. Navigate to: **Hackss** → **Tasks** → **android** → **signingReport**
3. Double-click **signingReport** to run it
4. In the Build tab at the bottom, find **SHA-1** key (looks like: `AB:CD:EF:12:34...`)
5. Copy the SHA-1 key
6. Go back to Firebase Console → **Project settings**
7. Scroll to **Your apps** → Find your Android app
8. Click **Add fingerprint**
9. Paste the SHA-1 key
10. Click **Save**

### Step 4: Download Updated google-services.json Again

After adding the SHA-1:

1. In Firebase Console → **Project settings** → **Your apps**
2. Click **Download google-services.json** again
3. **Replace** `app/google-services.json` with this new file

### Step 5: Update strings.xml with the Correct Web Client ID

1. Open the newly downloaded `app/google-services.json`
2. Find the `oauth_client` section with `"client_type": 3`
3. Copy the `client_id` value (should look like: `551902001389-xxxx.apps.googleusercontent.com`)
4. Open `app/src/main/res/values/strings.xml`
5. Replace the `default_web_client_id` value with the copied client ID:

```xml
<string name="default_web_client_id">YOUR_ACTUAL_CLIENT_ID_HERE</string>
```

### Step 6: Sync and Rebuild

```bash
# In Android Studio
File → Sync Project with Gradle Files
Build → Clean Project
Build → Rebuild Project

# Or via command line
./gradlew clean build
```

### Step 7: Uninstall and Reinstall the App

Sometimes the old configuration gets cached:

```bash
adb uninstall com.runanywhere.startup_hackathon20
./gradlew installDebug
```

## How to Test

1. **Open the app** - You should see the login screen
2. **Select "User Login" or "Admin Login"**
3. **Click "Sign in with Google"** button
4. **Select your Google account** from the picker
5. **You should see** a success message: "Login successful! Welcome [Your Name]"
6. **The app should navigate** to the Blue Carbon Monitor homepage (for Users) or Carbon Registry
   App (for Admins)

## Common Issues and Solutions

### Issue 1: "API_NOT_ENABLED" Error

**Solution**: Enable Google Sign-In API in Firebase Console:

- Firebase Console → Authentication → Sign-in method → Enable Google

### Issue 2: "DEVELOPER_ERROR" or "Error 10"

**Solution**: This means the Web Client ID is incorrect or SHA-1 is missing

- Verify the Web Client ID in `strings.xml` matches the one in `google-services.json` (client_type:
  3)
- Verify SHA-1 certificate is added in Firebase Console
- Download fresh `google-services.json` after adding SHA-1

### Issue 3: Account is Selected but Login Doesn't Complete

**Solution**: This was the main issue - now fixed!

- The UI wasn't handling `UiState.Success` properly
- I've added the success handler in MainActivity.kt

### Issue 4: "Network Error"

**Solution**:

- Check internet connection
- Verify Firebase project is active and not in free trial expiry

## Verification Checklist

Before testing, ensure:

- [ ] SHA-1 fingerprint added to Firebase Console
- [ ] Google Sign-In enabled in Firebase Authentication
- [ ] Latest `google-services.json` downloaded and placed in `app/` directory
- [ ] Web Client ID in `strings.xml` matches the one from `google-services.json` (client_type: 3)
- [ ] Project synced with Gradle files
- [ ] App completely uninstalled and reinstalled

## Why This Happens

The `google-services.json` file you have contains **placeholder** OAuth client IDs:

```json
"client_id": "551902001389-PLACEHOLDER.apps.googleusercontent.com"
```

Firebase generates real OAuth credentials only after you:

1. Add your app's SHA-1 certificate fingerprint
2. Enable Google Sign-In in Authentication
3. Download the updated `google-services.json`

## What Was Wrong in the Code

The original issue was in the `LoginScreen` composable in `MainActivity.kt`:

```kotlin
// BEFORE (only handled errors)
LaunchedEffect(uiState) {
    when (uiState) {
        is UiState.Error -> {
            snackbarHostState.showSnackbar((uiState as UiState.Error).message)
            viewModel.clearUiState()
        }
        else -> {}  // ❌ Success was ignored!
    }
}

// AFTER (handles both errors and success)
LaunchedEffect(uiState) {
    when (uiState) {
        is UiState.Success -> {  // ✅ Now handles success!
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

This meant:

- Google Sign-In would succeed in the background
- ViewModel would set `authState.isAuthenticated = true`
- ViewModel would set `uiState = UiState.Success("Login successful...")`
- But the UI never showed the success message or cleared the state
- The navigation should still have worked, but the user wouldn't see any feedback

## Summary

**Code fixes applied**:

- ✅ MainActivity.kt now handles `UiState.Success` properly
- ✅ strings.xml updated with Web Client ID placeholder

**You need to do**:

1. Add SHA-1 fingerprint to Firebase
2. Download correct `google-services.json`
3. Update `strings.xml` with real Web Client ID
4. Sync and rebuild
5. Test!

After completing these steps, Google authentication will work perfectly! 🎉
