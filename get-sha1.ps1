# Get SHA-1 Certificate Fingerprint for Firebase
# This script helps you quickly get the SHA-1 key needed for Google Sign-In

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Getting SHA-1 Certificate Fingerprint" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Change to project directory
$projectPath = "C:/Users/medhy/StudioProjects/Hackss"
Set-Location $projectPath

Write-Host "Running signingReport..." -ForegroundColor Yellow
Write-Host ""

# Run gradlew signingReport
./gradlew signingReport

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "Look for 'SHA1' in the output above" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "The SHA-1 key looks like:" -ForegroundColor Yellow
Write-Host "SHA1: AB:CD:EF:12:34:56:78:90:AB:CD:EF:12:34:56:78:90:AB:CD:EF:12" -ForegroundColor White
Write-Host ""
Write-Host "Copy this SHA-1 key and add it to Firebase Console:" -ForegroundColor Cyan
Write-Host "1. Go to https://console.firebase.google.com/" -ForegroundColor White
Write-Host "2. Select project: carbonchainplus" -ForegroundColor White
Write-Host "3. Click gear icon -> Project settings" -ForegroundColor White
Write-Host "4. Find your Android app" -ForegroundColor White
Write-Host "5. Click 'Add fingerprint'" -ForegroundColor White
Write-Host "6. Paste the SHA-1 key" -ForegroundColor White
Write-Host "7. Click Save" -ForegroundColor White
Write-Host ""
Write-Host "Press any key to exit..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
