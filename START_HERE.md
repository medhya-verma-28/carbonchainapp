# 🚀 START HERE - Carbon Registry App

## 📱 How to Run the App on Your Phone

### ✅ Simplest Way: Use Android Studio

1. Open **Android Studio**
2. Click **"Open"** → Select: `C:\Users\medhy\StudioProjects\Hackss`
3. Wait for Gradle sync (1-3 minutes)
4. Connect your phone via USB (enable USB debugging)
5. Click the **green Run ▶️ button** (or `Shift + F10`)
6. Select your device
7. Wait for build & install (~30-60 seconds)
8. **App launches automatically!** 🎉

---

## 📖 Documentation Files

- **[QUICK_START.md](QUICK_START.md)** - Fast setup guide with credentials
- **[RUN_APP.md](RUN_APP.md)** - Complete guide with troubleshooting
- **[README.md](README.md)** - Full app features and architecture

---

## 🧪 Test the App

### Login as User:

```
Username: user
Password: test123
```

→ Access Blue Carbon Monitor (photo upload, GPS tracking)

### Login as Admin:

```
Username: admin  
Password: admin123
```

→ Access full Carbon Registry (dashboard, projects, verification portal)

---

## 🎯 Key Features to Test

### As User:

- ✅ Capture photo with camera
- ✅ Upload from gallery
- ✅ Get GPS location
- ✅ Submit to admin verification

### As Admin:

- ✅ View dashboard statistics
- ✅ Browse carbon projects
- ✅ Manage carbon credits
- ✅ **Review user submissions** (Verification tab)
- ✅ Approve/reject submissions

---

## 🎨 What You'll See

- **Beautiful dark theme** with blue-green gradients
- **Glass effect** cards throughout
- **Smooth animations** and transitions
- **Modern Material 3** design
- **Professional UI/UX**

---

## ⚠️ Quick Troubleshooting

**Phone not detected?**

- Enable USB debugging: Settings → Developer Options
- Accept "Allow USB debugging" on phone
- Try different USB cable/port

**Build fails?**

- In Android Studio: Build → Clean Project → Rebuild
- Check Java is installed

**Need help?**

- See [RUN_APP.md](RUN_APP.md) for detailed troubleshooting

---

## 🏗️ Project Structure

```
Hackss/
├── app/
│   ├── src/main/
│   │   ├── java/.../
│   │   │   ├── MainActivity.kt          (Main UI)
│   │   │   ├── viewmodel/
│   │   │   │   └── CarbonViewModel.kt   (State management)
│   │   │   ├── repository/
│   │   │   │   └── CarbonRepository.kt  (Data layer)
│   │   │   ├── blockchain/
│   │   │   │   └── BlockchainService.kt (Wallet & TX)
│   │   │   └── ui/
│   │   │       └── AdminVerificationScreen.kt
│   │   └── res/
│   └── build.gradle.kts
├── RUN_APP.md              (Full guide)
├── QUICK_START.md          (Quick reference)
├── run-app.ps1             (PowerShell script)
└── README.md               (App overview)
```

---

## ✨ Latest Features

1. **Admin Verification Portal** - Review and approve user photo submissions
2. **Glass Effect UI** - Beautiful frosted glass design
3. **Role-Based Access** - Different views for User vs Admin
4. **Photo Documentation** - Camera + Gallery integration
5. **GPS Tracking** - Real-time location capture
6. **Blockchain Wallet** - Secure credit management

---

## 🚀 Ready? Let's Go!

**→ Open Android Studio**
**→ Open this project**
**→ Click Run ▶️**
**→ Test on your phone!**

---

**Build Time:**

- First build: 2-3 minutes
- Subsequent: 30-60 seconds

**Any questions?** Check [RUN_APP.md](RUN_APP.md) for complete guide!

🌱 **Built for a sustainable future!**
