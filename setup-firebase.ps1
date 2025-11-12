# Firebase Setup Helper Script
# This script helps you set up Firebase Google Sign-In

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Firebase Google Sign-In Setup Helper" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$projectPath = "C:/Users/medhy/StudioProjects/Hackss"
Set-Location $projectPath

# Step 1: Get SHA-1
Write-Host "STEP 1: Getting SHA-1 Certificate..." -ForegroundColor Yellow
Write-Host "Running: ./gradlew signingReport" -ForegroundColor Gray
Write-Host ""

./gradlew signingReport | Out-File -FilePath "signing-report.txt"

$sha1 = Get-Content "signing-report.txt" | Select-String "SHA1:" | Select-Object -First 1

if ($sha1) {
    Write-Host "✓ SHA-1 Found!" -ForegroundColor Green
    Write-Host $sha1 -ForegroundColor White
    Write-Host ""
    
    # Extract just the hash
    $sha1Value = ($sha1 -split "SHA1: ")[1].Trim()
    Set-Clipboard -Value $sha1Value
    Write-Host "✓ SHA-1 copied to clipboard!" -ForegroundColor Green
} else {
    Write-Host "✗ Could not find SHA-1. Check signing-report.txt" -ForegroundColor Red
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "STEP 2: Add SHA-1 to Firebase Console" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "1. Go to: https://console.firebase.google.com/" -ForegroundColor White
Write-Host "2. Select project: carbonchainplus" -ForegroundColor White
Write-Host "3. Click gear icon ⚙️ → Project settings" -ForegroundColor White
Write-Host "4. Find your app: com.runanywhere.startup_hackathon20" -ForegroundColor White
Write-Host "5. Click 'Add fingerprint'" -ForegroundColor White
Write-Host "6. Paste the SHA-1 (already in clipboard!)" -ForegroundColor White
Write-Host "7. Click Save" -ForegroundColor White
Write-Host ""

# Open Firebase Console
Write-Host "Opening Firebase Console in browser..." -ForegroundColor Yellow
Start-Process "https://console.firebase.google.com/"
Start-Sleep -Seconds 2

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "STEP 3: Enable Google Sign-In" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "After adding SHA-1:" -ForegroundColor White
Write-Host "1. Go to Authentication → Sign-in method" -ForegroundColor White
Write-Host "2. Click on Google" -ForegroundColor White
Write-Host "3. Toggle Enable ON" -ForegroundColor White
Write-Host "4. Enter your support email" -ForegroundColor White
Write-Host "5. Click Save" -ForegroundColor White
Write-Host ""

Write-Host "Press Enter after completing steps above..." -ForegroundColor Yellow
Read-Host

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "STEP 4: Download google-services.json" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "1. In Firebase Console → Project settings" -ForegroundColor White
Write-Host "2. Scroll to 'Your apps'" -ForegroundColor White
Write-Host "3. Click 'Download google-services.json'" -ForegroundColor White
Write-Host "4. Save it to your Downloads folder" -ForegroundColor White
Write-Host ""
Write-Host "Then, copy the file to:" -ForegroundColor Yellow
Write-Host "  $projectPath\app\google-services.json" -ForegroundColor White
Write-Host ""

Write-Host "Press Enter after downloading the file..." -ForegroundColor Yellow
Read-Host

# Check if new google-services.json exists
$googleServicesPath = "$projectPath\app\google-services.json"
if (Test-Path $googleServicesPath) {
    Write-Host "✓ google-services.json found!" -ForegroundColor Green
    
    # Try to extract Web Client ID
    $jsonContent = Get-Content $googleServicesPath -Raw | ConvertFrom-Json
    $oauthClients = $jsonContent.client[0].oauth_client
    $webClient = $oauthClients | Where-Object { $_.client_type -eq 3 }
    
    if ($webClient -and $webClient.client_id -notlike "*PLACEHOLDER*") {
        $webClientId = $webClient.client_id
        Write-Host "✓ Web Client ID found: $webClientId" -ForegroundColor Green
        Write-Host ""
        
        # Update strings.xml
        $stringsPath = "$projectPath\app\src\main\res\values\strings.xml"
        $stringsContent = Get-Content $stringsPath -Raw
        
        if ($stringsContent -match 'name="default_web_client_id">([^<]+)<') {
            $oldValue = $matches[1]
            $newContent = $stringsContent -replace $oldValue, $webClientId
            Set-Content -Path $stringsPath -Value $newContent
            
            Write-Host "✓ Updated strings.xml with Web Client ID!" -ForegroundColor Green
        }
    } else {
        Write-Host "✗ Web Client ID still has PLACEHOLDER value" -ForegroundColor Red
        Write-Host "Make sure you downloaded the file AFTER adding SHA-1!" -ForegroundColor Yellow
    }
} else {
    Write-Host "✗ google-services.json not found in app/ directory" -ForegroundColor Red
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "STEP 5: Rebuild and Install" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Cleaning project..." -ForegroundColor Yellow
./gradlew clean

Write-Host "Building project..." -ForegroundColor Yellow
./gradlew build

Write-Host ""
Write-Host "Uninstalling old app..." -ForegroundColor Yellow
adb uninstall com.runanywhere.startup_hackathon20 2>$null

Write-Host "Installing new app..." -ForegroundColor Yellow
./gradlew installDebug

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "✓ Setup Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Now test Google Sign-In in your app!" -ForegroundColor Cyan
Write-Host ""
Write-Host "If it still doesn't work, check:" -ForegroundColor Yellow
Write-Host "1. SHA-1 was added correctly in Firebase Console" -ForegroundColor White
Write-Host "2. Google Sign-In is enabled in Authentication" -ForegroundColor White
Write-Host "3. google-services.json has real OAuth client ID (not PLACEHOLDER)" -ForegroundColor White
Write-Host "4. strings.xml has the correct Web Client ID" -ForegroundColor White
Write-Host ""
Write-Host "See FIX_GOOGLE_AUTH.md for detailed troubleshooting" -ForegroundColor Gray
Write-Host ""

# Cleanup
Remove-Item "signing-report.txt" -ErrorAction SilentlyContinue
