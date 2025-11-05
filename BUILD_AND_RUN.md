# Build and Run Instructions

## Prerequisites

- Android Studio (Arctic Fox or newer)
- Android phone with USB debugging enabled
- USB cable to connect phone to computer

## Steps to Run on Phone

### 1. Open Project in Android Studio

1. Launch **Android Studio**
2. Click **File → Open**
3. Navigate to `C:/Users/medhy/StudioProjects/Hackss`
4. Click **OK**

### 2. Wait for Gradle Sync

- Android Studio will automatically sync Gradle
- Wait for "Gradle sync finished" message in the status bar
- This may take a few minutes on first run

### 3. Enable USB Debugging on Your Phone

#### For most Android phones:

1. Go to **Settings → About Phone**
2. Tap **Build Number** 7 times to enable Developer Mode
3. Go back to **Settings → Developer Options**
4. Enable **USB Debugging**
5. Connect phone to computer via USB
6. Authorize the computer when prompted on phone

### 4. Select Your Device

1. In Android Studio, look for the device dropdown (top toolbar)
2. Your phone should appear in the list
3. If not, click **Refresh** or **Troubleshoot device connections**

### 5. Run the App

1. Click the green **Run** button (▶️) in the toolbar
2. Or press `Shift + F10`
3. Android Studio will:
    - Build the APK
    - Install it on your phone
    - Launch the app automatically

## App Flow After Launch

### 1. **Login Screen** (appears first)

- Choose between **User Login** or **Admin Login**

### 2A. **User Login Path**

- Username: `user` (or register new account)
- Password: minimum 6 characters
- After login → **Blue Carbon Monitor** homepage
- Features:
    - Photo Documentation (Camera + Gallery)
    - Upload to Analysis
    - GPS Location tracking
    - Collection Status
    - Logout button (top right)

### 2B. **Admin Login Path**

- Username: `admin`
- Password: `admin123`
- After login → **Full Carbon Registry App**
- Access to all screens:
    - Dashboard
    - Projects
    - Credits
    - Wallet
    - Profile
    - **Verification Portal** (admin-only)

## Testing Admin Verification Portal

1. Login as **admin**
2. Navigate to **Verification** tab (bottom navigation)
3. View statistics: Pending, Approved, Rejected submissions
4. Click on any submission card to see details:
    - Submission image
    - Location, date, data quality
    - Analysis metrics (CO₂, hectares, vegetation %, AI confidence)
    - Verification checklist
5. For **PENDING** submissions:
    - Click **Approve & Publish** to upload to blockchain
    - Or **Reject** to decline with reason

## Theme Features

- ✅ Modern dark theme with deep blue/black gradients
- ✅ Green and blue accent colors
- ✅ Glass morphism effect on cards
- ✅ Animated gradient backgrounds
- ✅ Smooth transitions and animations

## Troubleshooting

### App won't install

- Make sure USB debugging is enabled
- Try **Build → Clean Project** then **Build → Rebuild Project**
- Check phone storage (need ~100MB free)

### Gradle sync fails

- Check internet connection (downloads dependencies)
- Invalidate caches: **File → Invalidate Caches / Restart**

### Camera/Location not working

- Grant permissions when prompted
- Check app permissions in phone settings

### Login not working

- For user: username must be 3+ chars, password 6+ chars
- For admin: exact credentials `admin` / `admin123`

## Uninstall

To remove the app from your phone:

1. Long press the app icon
2. Select **App info** → **Uninstall**
   Or via ADB: `adb uninstall com.runanywhere.startup_hackathon20`
