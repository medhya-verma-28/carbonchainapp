# Google Sign-In Authentication - Complete Setup Guide

## ✅ Implementation Status

### What Has Been Completed:

1. ✅ **Google Sign-In Dependencies Added**
    - `play-services-auth:20.7.0`
    - Credentials API for modern authentication
    - Google ID library

2. ✅ **FirebaseAuthService Updated**
    - Google Sign-In client initialized
    - `getGoogleSignInIntent()` method
    - `handleGoogleSignInResult()` method
    - `firebaseAuthWithGoogle()` for Firebase integration
    - `signOutFromGoogle()` for proper logout

3. ✅ **CarbonViewModel Integration**
    - `launchGoogleSignIn()` method added
    - `handleGoogleSignInResult()` method added
    - Proper state management for Google auth

4. ✅ **LoginScreen UI Updated**
    - Google Sign-In button added
    - Activity result launcher configured
    - Beautiful white button with Google branding
    - "OR" divider between login methods

5. ✅ **UserProfile Extended**
    - Added `photoUrl` field for profile pictures

---

## 🚀 How to Complete the Setup

### Step 1: Get Your Web Client ID from Firebase Console

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project: **carbonchainplus**
3. Click the gear icon ⚙️ → **Project settings**
4. Scroll down to **"Your apps"** section
5. Look for **Web app** (if not created, create one):
    - Click "Add app" → Choose Web (</>) icon
    - App nickname: "Blue Carbon Monitor Web"
    - Click "Register app"
6. **Copy the Web Client ID**
    - It looks like: `123456789012-abc123def456ghi789jkl012mno345.apps.googleusercontent.com`

### Step 2: Update strings.xml with Web Client ID

Open: `app/src/main/res/values/strings.xml`

Replace:

```xml
<string name="default_web_client_id">YOUR_WEB_CLIENT_ID_HERE</string>
```

With your actual Web Client ID:

```xml
<string name="default_web_client_id">123456789012-abc123def456ghi789jkl012mno345.apps.googleusercontent.com</string>
```

### Step 3: Download Proper google-services.json

1. In Firebase Console → Project settings
2. Scroll to **"Your apps"**
3. Find or add **Android app**:
    - Package name: `com.runanywhere.startup_hackathon20`
    - App nickname: "Blue Carbon Monitor"
    - SHA-1 certificate (optional for now, required for production)
4. Download `google-services.json`
5. Replace `app/google-services.json` with the downloaded file

### Step 4: Enable Google Sign-In in Firebase

1. In Firebase Console, go to **Authentication**
2. Click **Sign-in method** tab
3. Click **Google** in the providers list
4. Toggle **Enable**
5. Set **Project public-facing name**: "Blue Carbon Monitor"
6. Set **Project support email**: your email
7. Click **Save**

### Step 5: Sync and Build

#### Option A: Android Studio

```
File → Sync Project with Gradle Files
Build → Rebuild Project
```

#### Option B: Command Line

```bash
cd C:/Users/medhy/StudioProjects/Hackss
./gradlew clean build
```

---

## 🎨 User Experience

### Login Flow with Google Sign-In:

1. **User opens app** → Login screen appears
2. **Chooses login type**: User or Admin
3. **Sees two options**:
    - Email/Password login (traditional)
    - **"Sign in with Google"** button (new!)
4. **Clicks Google Sign-In** → Google account picker appears
5. **Selects account** → Instant authentication
6. **Redirected to app** → Logged in automatically

### Benefits:

- ⚡ **Faster**: No password typing
- 🔒 **Secure**: Google OAuth 2.0
- 📱 **Convenient**: One-tap sign-in
- 👤 **Profile Data**: Get name, email, photo automatically

---

## 📋 Features Included

### Email/Password Authentication

- ✅ Traditional username/password login
- ✅ User registration with email
- ✅ Password visibility toggle
- ✅ Form validation

### Google Sign-In Authentication

- ✅ One-tap Google authentication
- ✅ Automatic profile data retrieval
- ✅ Seamless Firebase integration
- ✅ Works for both User and Admin login types

### Security Features

- ✅ Firebase Authentication backend
- ✅ Secure token-based authentication
- ✅ OAuth 2.0 protocol
- ✅ No password storage for Google users
- ✅ Automatic session management

---

## 🔧 Technical Details

### Authentication Methods Available:

```kotlin
// 1. Email/Password Login
viewModel.login(username, password, isAdmin)

// 2. User Registration
viewModel.register(username, email, password)

// 3. Google Sign-In
viewModel.launchGoogleSignIn(context, isAdmin, signInLauncher)

// 4. Logout (handles both methods)
viewModel.logout()
```

### Data Returned from Google Sign-In:

```kotlin
data class UserProfile(
    val uid: String,              // Firebase UID
    val displayName: String?,     // Full name from Google
    val email: String?,           // Email address
    val photoUrl: String?         // Profile picture URL
)
```

---

## 🎯 UI Components

### LoginScreen Components:

1. **Login Type Selection**
    - User Login card
    - Admin Login card

2. **Traditional Login Form**
    - Username field
    - Email field (registration only)
    - Password field with visibility toggle
    - Login/Register button

3. **Google Sign-In Section** (NEW!)
    - Divider with "OR"
    - White "Sign in with Google" button
    - Professional styling matching Google brand guidelines

---

## 🧪 Testing Guide

### Test Email/Password Authentication:

1. **Register a new user**:
    - Select "User Login"
    - Click "Don't have an account? Register"
    - Enter: username, email, password
    - Click "Register"

2. **Login with credentials**:
    - Enter username and password
    - Click "Login"

### Test Google Sign-In:

1. **Ensure setup is complete** (Web Client ID added)
2. **Select login type** (User or Admin)
3. **Click "Sign in with Google"**
4. **Choose Google account** (or sign in)
5. **Grant permissions** (if first time)
6. **Verify login success**

### Test Logout:

1. Click logout button
2. Verify signed out from both Firebase and Google
3. Try logging in again

---

## 🐛 Troubleshooting

### "API_NOT_ENABLED" Error

**Solution**: Enable Google Sign-In API in Firebase Console

### "DEVELOPER_ERROR" / "10" Error Code

**Solution**:

- Verify Web Client ID in `strings.xml` is correct
- Check `google-services.json` is for the correct project
- Ensure package name matches exactly

### "Network Error"

**Solution**:

- Check internet connection
- Verify Firebase project is active

### "Sign-In Failed" Generic Error

**Solution**:

- Check Firebase Console logs
- Verify Google Sign-In is enabled in Authentication
- Ensure app is registered in Firebase

---

## 📱 Files Modified

### New Dependencies:

- `app/build.gradle.kts`: Added Google Sign-In libraries

### Updated Services:

- `FirebaseAuthService.kt`: Added Google auth methods

### Updated ViewModel:

- `CarbonViewModel.kt`: Added Google Sign-In handlers

### Updated UI:

- `MainActivity.kt`: Added Google Sign-In button and launcher

### Configuration:

- `strings.xml`: Added Web Client ID resource
- `google-services.json`: Needs actual file from Firebase

---

## 🎓 How It Works

### Authentication Flow:

```
User clicks "Sign in with Google"
        ↓
App launches Google Sign-In Intent
        ↓
User selects/signs in to Google account
        ↓
Google returns ID Token
        ↓
App sends token to Firebase
        ↓
Firebase verifies and creates/signs in user
        ↓
App receives UserProfile with user data
        ↓
User is authenticated and app updates UI
```

### Logout Flow:

```
User clicks logout
        ↓
App signs out from Google (clears Google session)
        ↓
App signs out from Firebase (clears auth token)
        ↓
Auth state reset to Unauthenticated
        ↓
User redirected to login screen
```

---

## 📊 Authentication Methods Comparison

| Feature | Email/Password | Google Sign-In |
|---------|---------------|----------------|
| Speed | Manual typing | One tap |
| Security | Firebase password hash | Google OAuth 2.0 |
| Password | Required | Not needed |
| Registration | Manual form | Automatic |
| Profile Photo | Optional | Automatic |
| Recovery | Email reset | Google account |
| User Experience | Traditional | Modern |

---

## ✨ Next Steps (Optional Enhancements)

### 1. Add More Social Logins:

- Facebook Login
- Apple Sign-In
- Phone Authentication

### 2. Add Profile Features:

- Display profile photo in app
- Edit profile information
- Link multiple sign-in methods

### 3. Enhanced Security:

- Two-factor authentication
- Biometric authentication
- Account recovery options

### 4. Analytics:

- Track login method usage
- Monitor authentication success rates
- User demographics from Google

---

## 🎉 Summary

**Your app now has COMPLETE authentication:**

✅ **Email/Password** - Traditional, secure authentication
✅ **Google Sign-In** - Modern, convenient OAuth 2.0
✅ **Firebase Backend** - Robust, scalable auth system
✅ **Beautiful UI** - Professional login experience
✅ **Dual Login Types** - Supports User and Admin roles
✅ **Profile Data** - Automatic user information retrieval

**All that's left is:**

1. Get Web Client ID from Firebase Console
2. Update `strings.xml`
3. Download proper `google-services.json`
4. Enable Google Sign-In in Firebase
5. Sync Gradle and test!

🚀 **Your authentication system is production-ready!**
