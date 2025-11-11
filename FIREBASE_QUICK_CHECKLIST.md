# Firebase Installation - Quick Checklist ✅

## Before You Start

- ✅ Authentication code already in project
- ✅ Firebase dependencies already added
- ✅ All necessary files already created

## 3 Simple Steps to Complete

### 1️⃣ Firebase Console (5 min)

- [ ] Go to https://console.firebase.google.com/
- [ ] Open project: **carbonchainplus**
- [ ] Add Android app if not exists:
    - Package: `com.runanywhere.startup_hackathon20`
- [ ] Download `google-services.json`
- [ ] Replace file at: `app/google-services.json`
- [ ] Enable **Email/Password** authentication
- [ ] Enable **Google Sign-In** authentication
- [ ] Copy **Web Client ID**

### 2️⃣ Update strings.xml (1 min)

- [ ] Open: `app/src/main/res/values/strings.xml`
- [ ] Find: `<string name="default_web_client_id">YOUR_WEB_CLIENT_ID_HERE</string>`
- [ ] Replace with your Web Client ID from Step 1

### 3️⃣ Sync & Build (2 min)

**Option A - Android Studio:**

- [ ] File → Sync Project with Gradle Files
- [ ] Build → Rebuild Project

**Option B - Command Line:**

```powershell
cd C:/Users/medhy/StudioProjects/Hackss
./gradlew clean build
```

## Test It!

- [ ] Run app on device
- [ ] Register with email/password
- [ ] Login with email/password
- [ ] Try "Sign in with Google"
- [ ] Check users in Firebase Console

## ✅ Done!

If all tests pass, Firebase is fully installed and working! 🎉

---

**Need help?** See: `FIREBASE_INSTALLATION_GUIDE.md`
