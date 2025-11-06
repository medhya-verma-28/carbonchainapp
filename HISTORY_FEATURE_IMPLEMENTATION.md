# History Feature & Dark Theme Implementation

## 🎯 Summary

This document outlines the implementation of the History side menu, improved dark theme, and
integrated user-admin workflow for the Blockchain Carbon Registry app.

## ✨ New Features Implemented

### 1. History Side Menu (User Login)

**Location**: `BlueCarbonMonitorHomepage` in `MainActivity.kt`

**Features**:

- Slide-in drawer navigation from the left side
- Shows all user carbon registry submissions
- Status-based color coding:
    - **Pending** (Cyan): Awaiting admin verification
    - **Approved/Registered** (Green): Published on blockchain
    - **Rejected** (Red): Did not meet requirements
- Click on any submission to view details

**Implementation**:

- Added `showHistoryDrawer` state variable
- Used `AnimatedVisibility` with slide animations
- LazyColumn displaying submissions with `HistoryItemCard` composable
- Separate dialogs for pending and approved submissions

### 2. Carbon Registry Portal (Approved Submissions)

**What it shows**:

- ✅ Registration Status (Registered/Pending/Rejected)
- 📊 Credit Details:
    - Credit Amount (in tCO₂)
    - Project Area (in hectares)
    - Vintage Year
    - Verification Date
- 📝 Smart Contract Information:
    - Contract Address
    - Network (Aptos Mainnet)
    - Token Standard (APT-20)
- 🔗 Transaction Hash (full blockchain hash)
- ✔️ Audit Trail (verification checklist)

**When visible**: Only when user clicks on an approved submission in History

### 3. Pending/Rejected Submission Messages

**Pending Submission**:

- Shows "⏳ Awaiting Verification" message
- Explains that admin approval is required
- Displays submission details (location, date, photo)

**Rejected Submission**:

- Shows "❌ Submission Rejected" message
- Displays rejection reason from admin
- Shows basic submission information

### 4. Modern Dark Theme with Green-Blue Gradient

**Updated Colors** (for better text visibility):

```kotlin
val DarkBackground = Color(0xFF07151A)  // Darker
val DarkSurface = Color(0xFF0A2326)     // Darker
val PrimaryGreen = Color(0xFF0EA676)    // Adjusted
val PrimaryTeal = Color(0xFF109893)     // Adjusted
val PrimaryBlue = Color(0xFF0894B4)     // Adjusted
val SecondaryGreen = Color(0xFF036D56)  // Darker
val AccentCyan = Color(0xFF14C9E6)      // Bright for highlights
val AccentEmerald = Color(0xFF1EC197)   // Adjusted
val TextPrimary = Color(0xFFFFFFFF)     // White text
val TextSecondary = Color(0xFFA6B9C4)   // Light grey
```

**Applied to**:

- Login screen background
- Blue Carbon Monitor background
- All glass effect surfaces
- Dialog backgrounds

### 5. Glass Effect Modifier

**Implementation**:

```kotlin
fun Modifier.glassEffect() = this
    .background(
        color = Color.White.copy(alpha = 0.1f),
        shape = RoundedCornerShape(20.dp)
    )
    .border(
        width = 1.dp,
        color = Color.White.copy(alpha = 0.2f),
        shape = RoundedCornerShape(20.dp)
    )
```

**Used throughout**:

- All card surfaces in Blue Carbon Monitor
- History drawer surface
- Dialog backgrounds
- Login form containers

## 🔄 Integrated Workflow

### User → Admin Flow

1. **User submits data** via `submitCarbonRegistry()` in `CarbonViewModel`
2. **Submission created** in `CarbonRepository` with status `PENDING`
3. **Admin sees submission** in Verification Portal
4. **Admin approves/rejects**:
    - Approve: Creates `CarbonRegistrySubmission` with blockchain details
    - Reject: Updates status to `REJECTED` with notes
5. **User sees update** in History menu:
    - Pending → Shows waiting message
    - Approved → Shows full registry portal
    - Rejected → Shows rejection reason

### Data Models

**New Data Class**: `CarbonRegistrySubmission`

```kotlin
data class CarbonRegistrySubmission(
    val id: String,
    val registrationStatus: RegistrationStatus,
    val blockNumber: String,
    val creditAmount: Double,
    val projectArea: String,
    val vintageYear: Int,
    val verificationDate: String,
    val transactionHash: String,
    val contractAddress: String,
    val network: String,
    val tokenStandard: String,
    val auditTrail: List<AuditItem>,
    val registryNotes: String,
    val imageUrl: String?,
    val location: String,
    val coordinates: Coordinates?,
    val submissionDate: Long,
    val submitterName: String,
    val submitterEmail: String,
    val status: SubmissionStatus
)
```

**New Enum**: `RegistrationStatus`

```kotlin
enum class RegistrationStatus {
    REGISTERED,
    PENDING,
    REJECTED
}
```

**New Data Class**: `AuditItem`

```kotlin
data class AuditItem(
    val description: String,
    val completed: Boolean
)
```

## 📁 Files Modified

### 1. `data/CarbonCredit.kt`

- Added `CarbonRegistrySubmission` data class
- Added `AuditItem` data class
- Added `RegistrationStatus` enum

### 2. `repository/CarbonRepository.kt`

- Added `_carbonRegistrySubmissions` StateFlow
- Updated `approveSubmission()` to create `CarbonRegistrySubmission`
- Added `submitCarbonRegistry()` method
- Added `getUserSubmissions()` and `getCarbonRegistrySubmissions()` methods
- Added mock data for approved registry submission

### 3. `viewmodel/CarbonViewModel.kt`

- Added `carbonRegistrySubmissions` StateFlow
- Added `submitCarbonRegistry()` method
- Added `getUserCarbonRegistries()` method
- Added `getUserPendingSubmissions()` method

### 4. `MainActivity.kt`

- Updated color scheme to darker theme
- Added History drawer to `BlueCarbonMonitorHomepage`
- Created `HistoryItemCard` composable
- Updated `uploadToAnalysis()` to use `viewModel.submitCarbonRegistry()`
- Added dialogs for pending and approved submissions
- Updated background gradients throughout
- Added glass effect to all surfaces
- Changed History icon to `Icons.Default.List`

### 5. `README.md`

- Documented new History feature
- Added usage instructions for users and admins
- Updated UI/UX highlights
- Added integrated workflow section
- Documented Carbon Registry Portal

## 🎨 Design Decisions

### Why Darker Gradients?

- **Better Text Visibility**: Darker backgrounds provide higher contrast for white/light text
- **Reduced Eye Strain**: Darker colors are easier on the eyes in low-light environments
- **Professional Appearance**: Dark themes with subtle gradients look more modern and premium
- **Glass Effect Enhancement**: Darker backgrounds make glass effects more prominent

### Why Side Drawer for History?

- **Non-intrusive**: Doesn't take up main screen real estate
- **Quick Access**: Easy to open/close with swipe gesture
- **Familiar Pattern**: Users are accustomed to drawer navigation in mobile apps
- **Contextual**: Available only in Blue Carbon Monitor where it's relevant

### Why Separate Portal View?

- **Information Density**: Registry details are complex and need dedicated space
- **Progressive Disclosure**: Users see summary in History, full details on click
- **Matches Reference Design**: Follows the portal layout from the provided image
- **Clear Hierarchy**: Organized sections for different types of information

## 🧪 Testing Checklist

- [ ] Login as user and submit carbon registry data
- [ ] Check History drawer opens/closes smoothly
- [ ] Verify pending submission shows waiting message
- [ ] Login as admin and approve submission
- [ ] Return to user and verify approved submission shows portal
- [ ] Check all portal sections display correctly
- [ ] Test rejection workflow with notes
- [ ] Verify text is clearly visible on all screens
- [ ] Test glass effects render properly
- [ ] Check animations are smooth

## 📝 Future Enhancements

- [ ] Add swipe-to-open gesture for History drawer
- [ ] Implement pull-to-refresh for submissions list
- [ ] Add filtering options in History (Pending/Approved/Rejected)
- [ ] Export registry portal as PDF
- [ ] Add blockchain verification button
- [ ] Implement search in History
- [ ] Add submission timeline view
- [ ] Share registry portal via link

## 🎓 Key Learnings

1. **StateFlow Management**: Using derived state for user-specific data
2. **Compose Animations**: Implementing slide-in drawer with AnimatedVisibility
3. **Dialog Composition**: Creating different dialogs based on submission status
4. **Theme Consistency**: Maintaining consistent color scheme across all screens
5. **Data Transformation**: Converting UserSubmission to CarbonRegistrySubmission on approval
6. **Glass Morphism**: Implementing glass effects with proper alpha and blur values

---

**Implementation Date**: 2024  
**Platform**: Android (Jetpack Compose)  
**Minimum SDK**: 24  
**Target SDK**: 34
