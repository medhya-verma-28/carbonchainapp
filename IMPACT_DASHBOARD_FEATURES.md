# Impact Dashboard Features Implementation

## Overview

This document describes the implementation of two key features in the Impact Dashboard:

1. **View Detailed Analysis** - Downloads a comprehensive PDF report
2. **New Monitoring Project** - Returns to Blue Carbon Monitor homepage

## Implementation Details

### 1. View Detailed Analysis (PDF Generation)

#### What it does:

When clicked, this button generates and downloads a comprehensive PDF report containing all
monitoring project details including:

- Photo documentation from the captured image
- Project overview (location, submitter, coordinates)
- Impact metrics (carbon reduced, market value, etc.)
- Progress indicators
- Environmental health metrics
- Community benefits
- Monitoring details
- Blockchain registry details
- Audit trail
- Sustainability message

#### Technical Implementation:

- **Library Used**: iText 7.2.5 for PDF generation
- **File Location**: Downloads folder on the device
- **File Naming**: `BlueCarbon_Impact_Report_YYYYMMDD_HHMMSS.pdf`
- **Permissions**:
    - For Android < 10: Requests `WRITE_EXTERNAL_STORAGE` permission
    - For Android 10+: Uses MediaStore API (no permission needed)

#### Key Components:

- `generateProjectPDF()` - Main function to create PDF file
- `addPDFContent()` - Formats and adds content to PDF
- Storage permission launcher for older Android versions
- Image conversion from URI to bitmap for inclusion in PDF

### 2. New Monitoring Project

#### What it does:

When clicked, this button:

- Closes the Impact Dashboard dialog
- Marks the current submission as completed
- Clears the dashboard data and selected registry
- Returns user to the Blue Carbon Monitor homepage
- Shows a toast message confirming the action

#### Technical Implementation:

- Resets all impact dashboard state variables
- Calls `viewModel.markSubmissionAsCompleted()` to update submission status
- User remains in the BlueCarbonMonitorHomepage composable (no navigation needed)
- Ready to start a new monitoring project immediately

## Files Modified

### 1. `app/build.gradle.kts`

- Added iText PDF library dependency: `com.itextpdf:itext7-core:7.2.5`
- Added multidex support: `androidx.multidex:multidex:2.0.1`
- Enabled multidex in defaultConfig

### 2. `app/src/main/java/com/runanywhere/startup_hackathon20/MainActivity.kt`

- Added PDF generation imports (iText, Android graphics, file handling)
- Created `generateProjectPDF()` function
- Created `addPDFContent()` helper function
- Added storage permission launcher in `BlueCarbonMonitorHomepage`
- Updated "View Detailed Analysis" button with PDF generation logic
- Updated "New Monitoring Project" button to close dashboard and reset state

### 3. `app/src/main/AndroidManifest.xml`

- Already had required permissions (no changes needed):
    - `WRITE_EXTERNAL_STORAGE` (for Android < 10)
    - `READ_EXTERNAL_STORAGE`

## PDF Content Structure

The generated PDF includes:

1. **Header**
    - Title: "Blue Carbon Monitor Impact Dashboard Report"
    - Generation timestamp

2. **Photo Documentation**
    - Captured image from the monitoring project
    - Automatically converted from URI to embedded JPEG

3. **Project Overview Table**
    - Location
    - Submitter name and email
    - Submission date
    - GPS coordinates

4. **Impact Metrics Table**
    - Carbon Reduced (tCO₂)
    - Market Value ($)
    - Monitoring Period
    - Monitoring Date
    - Carbon Sequestered (kg)
    - CO₂e Reduction (kg)
    - People Impacted
    - Trees Planted

5. **Progress Indicators**
    - Monitoring Progress (%)
    - Stakeholder Engagement (%)
    - Compliance Tracking (%)
    - Biodiversity (%)

6. **Environmental Health**
    - Water Quality status
    - Habitat Health status
    - Service Life status

7. **Community Benefits**
    - Families Supported
    - Jobs Created

8. **Monitoring Details Table**
    - Duration of Project
    - Next Monitoring
    - Last Reported
    - Project Membership

9. **Blockchain Registry Details Table**
    - Registration Status
    - Block Number
    - Credit Amount
    - Project Area
    - Vintage Year
    - Verification Date
    - Transaction Hash
    - Contract Address
    - Network
    - Token Standard

10. **Audit Trail**
    - List of completed audit items

11. **Sustainability Message**
    - Custom sustainability impact message

12. **Footer**
    - "Blue Carbon Monitor - Environmental Impact Tracking System"

## Usage

### For Users:

1. **To Download PDF Report:**
    - Complete a monitoring project through to the Impact Dashboard
    - Click "View Detailed Analysis" button
    - On first use (Android < 10), grant storage permission when prompted
    - PDF will be saved to Downloads folder
    - Toast message confirms successful download with filename

2. **To Start New Project:**
    - From Impact Dashboard, click "New Monitoring Project" button
    - Dashboard closes automatically
    - You're back at Blue Carbon Monitor homepage
    - Ready to capture new photo and start new monitoring

### Error Handling:

- **Missing Data**: Shows "Project data not available" message
- **Permission Denied**: Shows "Storage permission required to save PDF" message
- **PDF Generation Failed**: Shows error message with details
- **Image Not Available**: PDF includes placeholder text instead

## Testing Recommendations

1. Test PDF generation with valid project data
2. Test PDF generation without project data
3. Test storage permission flow on Android < 10
4. Test on Android 10+ (no permission needed)
5. Verify PDF content includes all sections
6. Verify image is properly embedded in PDF
7. Test "New Monitoring Project" button closes dashboard
8. Verify new project can be started after closing dashboard
9. Test with long text content (wrapping, pagination)
10. Test with projects that have no image

## Build Information

- **Build Status**: ✅ Successful
- **Multidex**: Enabled to support iText library
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36 (Android 15)

## Notes

- PDF files are saved with timestamps to prevent overwriting
- Images are compressed to JPEG 80% quality for reasonable file size
- PDF uses app theme colors (PrimaryGreen, PrimaryBlue) for branding
- All data formatting uses locale-appropriate number formats
- Proper error handling prevents app crashes if PDF generation fails
