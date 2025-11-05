# ⚡ Quick Start - Run App on Phone

## 🎯 Easiest Method (RECOMMENDED)

### Using Android Studio:

1. **Open Android Studio**
2. **Open Project:** `C:\Users\medhy\StudioProjects\Hackss`
3. **Wait for Gradle Sync** (1-3 minutes)
4. **Connect Phone/Emulator:**
    - Enable USB debugging on phone
    - Or start an emulator
5. **Click Run ▶️** (or press `Shift + F10`)
6. **Select Device** and click OK

**Done! App launches automatically.** 🎉

---

## ⚡ Alternative: PowerShell Script

If you prefer command line:

```powershell
# Connect your phone first, then run:
.\run-app.ps1
```

This script will:

- ✅ Check device connection
- ✅ Build the APK
- ✅ Install on device
- ✅ Launch the app

---

## 🧪 Test Login Credentials

### 👤 User Login (Blue Carbon Monitor)

```
Username: user
Password: test123
```

**Features:**

- 📸 Capture/upload photos
- 📍 GPS location tracking
- ⬆️ Submit to admin verification

### ⚙️ Admin Login (Full Carbon Registry)

```
Username: admin
Password: admin123
```

**Features:**

- 📊 Dashboard with statistics
- 🌱 Carbon projects browser
- 💳 Credits management
- 💼 Blockchain wallet
- 👤 Profile
- ✅ **Verification Portal** (review user submissions)

---

## 📱 Enable USB Debugging on Phone

1. **Settings** → **About Phone**
2. Tap **Build Number** 7 times (enables Developer Options)
3. **Settings** → **Developer Options**
4. Enable **USB Debugging**
5. Connect USB cable
6. Accept "Allow USB debugging?" prompt

---

## ⚠️ Troubleshooting

**Device not detected?**

```powershell
C:\Users\medhy\AppData\Local\Android\Sdk\platform-tools\adb.exe devices
```

**Build fails?**

- In Android Studio: **Build** → **Clean Project** → **Rebuild Project**

**App crashes?**

- Check Logcat in Android Studio
- Ensure permissions are granted (Camera, Location)

---

## 📖 Full Guide

For detailed instructions, troubleshooting, and complete feature list:

- See **[RUN_APP.md](RUN_APP.md)** for comprehensive guide
- See **[README.md](README.md)** for app features and architecture

---

## ✨ What to Expect

### Beautiful UI:

- 🎨 Dark blue-black gradient theme
- 💎 Frosted glass effect cards
- ✨ Smooth animations
- 🎯 Modern Material 3 design

### Latest Features:

- ✅ Admin verification portal
- ✅ User photo submissions
- ✅ GPS tracking
- ✅ Camera + gallery support
- ✅ Role-based access (User/Admin)
- ✅ Blockchain wallet integration

---

**Build Time:** 30-60 seconds (first build: 2-3 minutes)

**Ready to go!** 🚀
