# Install App on Xiaomi Phone - Quick Guide

## ⚡ Fastest Method: Using Android Studio

### Step 1: Connect Your Xiaomi Phone

1. **Enable Developer Options**:
    - Open **Settings** on Xiaomi phone
    - Go to **About phone**
    - Tap **MIUI version** 7 times rapidly
    - You'll see "You are now a developer!"

2. **Enable USB Debugging**:
    - Go back to **Settings**
    - Open **Additional settings**
    - Select **Developer options**
    - Enable **USB debugging**
    - Enable **Install via USB** (if available)

3. **Connect Phone**:
    - Connect Xiaomi phone to computer with USB cable
    - On phone, select **File Transfer** mode
    - Allow USB debugging when prompted (check "Always allow")

### Step 2: Install from Android Studio

1. **Open Android Studio**
2. **Open this project** (if not already open)
3. **Wait for Gradle sync** to complete (bottom status bar)
4. **Select your Xiaomi device**:
    - Look at the top toolbar
    - Click the device dropdown (next to Run button)
    - Your Xiaomi phone should appear in the list
    - Select it

5. **Click the green Run button** (▶) or press `Shift+F10`

6. **Wait for build and installation**:
    - Android Studio will build the APK
    - Install it on your phone
    - Launch the app automatically

### Step 3: Grant Permissions (if needed)

When the app starts, it may ask for permissions:

- ✅ **Camera** - Tap "Allow"
- ✅ **Location** - Tap "Allow"
- ✅ **Storage/Photos** - Tap "Allow"

**Done!** The app should now be running on your Xiaomi phone.

---

## 🔧 Alternative: Manual Installation

If Android Studio doesn't detect your phone:

### Step 1: Build APK

In Android Studio, open **Terminal** (bottom panel) and run:

```bash
gradlew assembleDebug
```

Wait for build to complete (2-3 minutes).

### Step 2: Find APK

APK will be at:

```
app\build\outputs\apk\debug\app-debug.apk
```

### Step 3: Transfer and Install

**Method A - Direct USB Transfer**:

1. Open File Explorer
2. Navigate to `app\build\outputs\apk\debug\`
3. Copy `app-debug.apk` to your Xiaomi phone (drag and drop)
4. On phone, open Files app
5. Find and tap `app-debug.apk`
6. Tap "Install"
7. If blocked, enable "Install from unknown sources" in settings

**Method B - ADB Install** (if ADB is set up):

```bash
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

---

## 🚨 Xiaomi MIUI Troubleshooting

### Issue: "Installation Blocked"

**Solution**:

1. Settings → **Additional settings** → **Privacy**
2. **Install apps from external sources**
3. Enable for **Files** or **USB debugging**

### Issue: "App Not Installed"

**Solution**:

1. Uninstall old version first (if exists)
2. Settings → **Apps** → **Startup Hackathon** → **Uninstall**
3. Try installing again

### Issue: Android Studio Doesn't See Phone

**Solution**:

1. Try different USB cable (some cables are charge-only)
2. Try different USB port
3. Unplug and replug phone
4. In Developer options, click "Revoke USB debugging authorizations"
5. Reconnect phone and accept prompt again

### Issue: MIUI Optimization Blocking

**Solution**:

1. Developer options → Disable "MIUI optimization"
2. Phone will restart
3. Reinstall app

### Issue: App Crashes After Install

**Solution**:

1. Settings → **Apps** → **Startup Hackathon**
2. **Permissions** → Enable all
3. **Battery saver** → **No restrictions**
4. **Autostart** → Enable

---

## ✅ Verification Checklist

After installation, verify:

- [ ] App icon appears on home screen
- [ ] App opens without crashing
- [ ] Camera works (test in Carbon Registry section)
- [ ] Location works (test in Carbon Registry section)
- [ ] No error messages

---

## 📱 Current Installation Steps

**Right now, do this**:

1. ✅ Connect your Xiaomi phone via USB
2. ✅ Open Android Studio
3. ✅ Select your device from dropdown
4. ✅ Click Run button (green play icon)
5. ✅ Wait 2-3 minutes
6. ✅ Grant permissions when asked
7. ✅ App should be running!

**That's it!** Android Studio handles everything else.

---

## 🆘 Need Help?

### Device Not Showing in Android Studio?

1. Check USB debugging is enabled
2. Check phone is in "File Transfer" mode (not "Charging only")
3. Accept USB debugging prompt on phone
4. Try: File → Invalidate Caches → Restart in Android Studio

### Build Failed?

1. Build → Clean Project
2. Build → Rebuild Project
3. Try again

### Still Having Issues?

1. Close Android Studio
2. Disconnect phone
3. Restart Android Studio
4. Reconnect phone
5. Try again

---

## Quick Reference

| Action | Steps |
|--------|-------|
| **Connect Phone** | USB debugging ON → Connect USB → Select "File Transfer" |
| **Install** | Android Studio → Select device → Run button |
| **Reinstall** | Build → Clean Project → Run again |
| **Grant Permissions** | Settings → Apps → Startup Hackathon → Permissions → Enable all |
| **Check Logs** | Android Studio → Logcat tab (bottom panel) |

---

**Ready?** Connect your Xiaomi phone and click the Run button in Android Studio! 🚀
