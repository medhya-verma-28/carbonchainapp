# Firebase Authentication Integration

## Overview

This document describes the complete integration of Firebase Authentication into the Blue Carbon
Monitor app for secure user login and registration.

## Firebase Configuration

### Project Details

- **Project ID**: carbonchainplus
- **Project Number**: 551902001389
- **App ID**: 1:551902001389:android:d147b376098afb1b5bf4e4
- **Storage Bucket**: carbonchainplus.firebasestorage.app
- **API Key**: AIzaSyBJK9YfQUeiYuYsqhiffmx3VWIRbxt4VKs

## Files Modified/Created

### 1. Build Configuration Files

#### `build.gradle.kts` (Root)

- Added Google Services plugin: `com.google.gms.google-services` version 4.4.0

#### `app/build.gradle.kts`

- Applied Google Services plugin
- Added Firebase dependencies:
    - Firebase BoM (Bill of Materials) 32.7.0
    - Firebase Authentication
    - Firebase Firestore
    - Firebase Analytics
    - Firebase Storage

### 2. Firebase Configuration File

#### `app/google-services.json`

- Created Firebase configuration file with project credentials
- Contains API keys, project IDs, and app identifiers
- Required for Firebase SDK initialization

### 3. Firebase Authentication Service

#### `app/src/main/java/com/runanywhere/startup_hackathon20/firebase/FirebaseAuthService.kt`

- Comprehensive Firebase Authentication service
- Features:
    - User login with email/password
    - User registration with username, email, password
    - Logout functionality
    - Password reset
    - Email update
    - Password update
    - Account deletion
    - Real-time auth state monitoring

### 4. ViewModel Integration

#### `app/src/main/java/com/runanywhere/startup_hackathon20/viewmodel/CarbonViewModel.kt`

- Integrated FirebaseAuthService into CarbonViewModel
- Updated login() method to use Firebase authentication
- Updated register() method to use Firebase authentication
- Updated logout() method to use Firebase sign out
- User credentials now stored securely in Firebase

## Implementation Details

### Authentication Flow

#### 1. User Registration

```kotlin
// User enters username, email, and password
viewModel.register(username, email, password)

// Firebase creates account:
// - Email format: username@carbonchain.app (if no @ provided)
// - Password must be at least 6 characters
// - DisplayName set to username
// - Returns UserProfile with uid, displayName, email
```

#### 2. User Login

```kotlin
// User enters username/email and password
viewModel.login(username, password, isAdmin = false)

// Firebase authenticates:
// - Converts username to email format if needed
// - Validates credentials
// - Returns UserProfile on success
// - Updates authState in ViewModel
```

#### 3. User Logout

```kotlin
// User logs out
viewModel.logout()

// Firebase signs out:
// - Clears current user session
// - Resets auth state
// - Returns to login screen
```

### Features

#### Email Compatibility

The system automatically converts usernames to email format:

- Input: `john123` → `john123@carbonchain.app`
- Input: `john@example.com` → `john@example.com`

This allows users to login with either username or email.

#### Security Features

- Passwords are hashed and stored securely by Firebase
- No plain-text password storage
- Secure token-based authentication
- Automatic session management
- Protection against brute force attacks

#### Real-time Auth State

The app monitors authentication state in real-time:

```kotlin
firebaseAuth.authState.collect { state ->
    when (state) {
        is AuthState.Unauthenticated -> // Show login
        is AuthState.Loading -> // Show loading
        is AuthState.Authenticated -> // Show main app
        is AuthState.Error -> // Show error message
    }
}
```

## User Data Structure

### UserProfile

```kotlin
data class UserProfile(
    val uid: String,          // Firebase unique user ID
    val displayName: String?, // Username chosen during registration
    val email: String?        // User's email address
)
```

### AuthState (ViewModel)

```kotlin
data class AuthState(
    val isAuthenticated: Boolean = false,
    val userType: UserType? = null,  // USER or ADMIN
    val username: String? = null,
    val email: String? = null
)
```

## Additional Features Available

### Password Reset

```kotlin
suspend fun sendPasswordResetEmail(email: String): Result<Unit>
// Sends password reset email to user
```

### Update Email

```kotlin
suspend fun updateUserEmail(newEmail: String): Result<Unit>
// Updates user's email address
```

### Update Password

```kotlin
suspend fun updateUserPassword(newPassword: String): Result<Unit>
// Updates user's password
```

### Delete Account

```kotlin
suspend fun deleteUserAccount(): Result<Unit>
// Permanently deletes user account
```

## Firebase Console Setup

### Required Setup Steps

1. **Enable Authentication Methods**
    - Go to Firebase Console → Authentication → Sign-in method
    - Enable "Email/Password" authentication
    - Save changes

2. **Configure Authorized Domains**
    - Go to Authentication → Settings → Authorized domains
    - Ensure your app domain is authorized (usually automatic)

3. **Set Up Firestore (Optional)**
    - Go to Firestore Database
    - Create database
    - Choose production or test mode
    - Set up security rules

4. **Enable Analytics (Optional)**
    - Analytics is automatically enabled
    - View user engagement in Firebase Console

## Security Rules

### Firestore Rules (Recommended)

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can only read/write their own data
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Carbon submissions
    match /submissions/{submissionId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null;
      allow update, delete: if request.auth != null && 
                               request.auth.uid == resource.data.userId;
    }
  }
}
```

### Storage Rules (Recommended)

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /users/{userId}/{allPaths=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    match /photos/{allPaths=**} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.resource.size < 5 * 1024 * 1024;
    }
  }
}
```

## Error Handling

The Firebase integration includes comprehensive error handling:

```kotlin
// Login errors
- "Invalid credentials"
- "User not found"
- "Wrong password"
- "Network error"

// Registration errors
- "Email already in use"
- "Weak password"
- "Invalid email"
- "Network error"
```

All errors are caught and displayed to the user via Toast messages.

## Testing

### Test Accounts

For development/testing, you can create test accounts:

1. **Admin Account**
    - Can be created in Firebase Console
    - Add custom claim: `admin: true`

2. **Regular User Account**
    - Created through app registration
    - Default user type

### Testing Checklist

- [ ] User can register with username, email, password
- [ ] User can login with username or email
- [ ] User can logout successfully
- [ ] Auth state persists across app restarts
- [ ] Password reset email is sent
- [ ] Invalid credentials show error message
- [ ] Network errors are handled gracefully
- [ ] User data is stored securely in Firebase

## Migration from Local Auth

The existing local authentication system has been replaced with Firebase:

**Before:**

- Usernames/passwords stored locally
- Simple validation (admin/admin123)
- No password recovery
- No secure storage

**After:**

- Credentials stored in Firebase
- Secure password hashing
- Password recovery available
- Token-based authentication
- Real-time sync across devices

## Next Steps

1. **Sync Gradle**
   ```bash
   ./gradlew build
   ```

2. **Enable Firebase Auth in Console**
    - Visit https://console.firebase.google.com/
    - Select "carbonchainplus" project
    - Enable Email/Password authentication

3. **Test Authentication**
    - Register a new user
    - Login with credentials
    - Test logout
    - Test error cases

4. **Optional: Add Social Login**
    - Google Sign-In
    - Facebook Login
    - Phone Authentication

## Benefits

✅ **Secure Authentication** - Industry-standard security
✅ **Password Recovery** - Users can reset passwords
✅ **Scalability** - Handles millions of users
✅ **Real-time Sync** - Auth state synced instantly
✅ **No Backend Required** - Firebase handles everything
✅ **Analytics** - Track user engagement
✅ **Cloud Storage** - Store user data securely
✅ **Cross-platform** - Share auth across Android/iOS/Web

## Support

For issues or questions:

1. Check Firebase Console for errors
2. View logs in Android Studio
3. Check Firebase Authentication documentation
4. Review error messages in app

## Version Information

- **Firebase BoM**: 32.7.0
- **Firebase Auth**: Latest (via BoM)
- **Google Services Plugin**: 4.4.0
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36 (Android 15)
