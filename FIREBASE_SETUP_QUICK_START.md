# Firebase Setup Quick Start

## ⚡ Quick Setup (5 minutes)

### Step 1: Sync Gradle Dependencies

```bash
./gradlew build
```

Wait for dependencies to download and sync.

### Step 2: Enable Firebase Authentication

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select project **"carbonchainplus"**
3. Click **"Authentication"** in left menu
4. Click **"Get started"** (if first time)
5. Go to **"Sign-in method"** tab
6. Click on **"Email/Password"**
7. Toggle **"Enable"** switch
8. Click **"Save"**

### Step 3: Test the App

1. Run the app on your device/emulator
2. Try registering a new user:
    - Username: `testuser`
    - Email: `test@example.com`
    - Password: `test123456`
3. Logout and login again
4. Check Firebase Console → Authentication → Users to see registered user

## 🎯 What Changed

### Files Added/Modified:

- ✅ `app/google-services.json` - Firebase configuration
- ✅ `app/src/main/java/com/runanywhere/startup_hackathon20/firebase/FirebaseAuthService.kt` - Auth
  service
- ✅ `app/build.gradle.kts` - Firebase dependencies
- ✅ `build.gradle.kts` - Google Services plugin
- ✅ `CarbonViewModel.kt` - Integrated Firebase auth

### New Dependencies:

- Firebase BoM 32.7.0
- Firebase Authentication
- Firebase Firestore
- Firebase Analytics
- Firebase Storage

## 🔑 Key Features

1. **Secure Authentication**
    - Passwords encrypted by Firebase
    - No plain-text storage
    - Industry-standard security

2. **Username OR Email Login**
    - Enter username: converts to `username@carbonchain.app`
    - Enter email: uses email directly

3. **Password Recovery**
    - Users can reset forgotten passwords
    - Reset email sent by Firebase

4. **Real-time Sync**
    - Auth state synced instantly
    - Works across devices

## 🐛 Troubleshooting

### Build Fails

```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

### Firebase Not Working

1. Check `google-services.json` is in `app/` folder
2. Verify Email/Password enabled in Firebase Console
3. Check internet connection
4. View logs in Android Studio Logcat

### Login Errors

- **"Email already in use"** → Use different email/username
- **"Weak password"** → Password must be 6+ characters
- **"Network error"** → Check internet connection
- **"Invalid credentials"** → Check username/password

## 📱 Usage Examples

### Register New User

```kotlin
viewModel.register("john", "john@example.com", "password123")
```

### Login

```kotlin
viewModel.login("john", "password123", isAdmin = false)
```

### Logout

```kotlin
viewModel.logout()
```

## ✅ Verification Checklist

- [ ] `google-services.json` file exists in `app/` folder
- [ ] Gradle sync successful
- [ ] Firebase Email/Password authentication enabled
- [ ] App runs without crashes
- [ ] Can register new user
- [ ] Can login with credentials
- [ ] Can logout successfully
- [ ] User appears in Firebase Console

## 🚀 You're All Set!

The app now uses Firebase for secure authentication. Users can:

- ✅ Register with username, email, password
- ✅ Login with username or email
- ✅ Logout securely
- ✅ Reset passwords (feature available)
- ✅ Have data synced to cloud

## 📚 Next Steps

1. Test all authentication flows
2. Add more users in Firebase Console
3. Set up Firestore for data storage (optional)
4. Configure security rules (see FIREBASE_INTEGRATION.md)
5. Add social login (Google, Facebook) if desired

## 🔗 Useful Links

- [Firebase Console](https://console.firebase.google.com/)
- [Firebase Auth Documentation](https://firebase.google.com/docs/auth)
- [Full Integration Guide](./FIREBASE_INTEGRATION.md)
