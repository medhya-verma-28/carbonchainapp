# Update App Icon Script
Write-Host "Updating App Icon..." -ForegroundColor Cyan

$source = "app/src/main/res/drawable/app_logo.png"

if (-not (Test-Path $source)) {
    Write-Host "Error: app_logo.png not found" -ForegroundColor Red
    exit 1
}

$densities = @("mdpi", "hdpi", "xhdpi", "xxhdpi", "xxxhdpi")

foreach ($d in $densities) {
    $dir = "app/src/main/res/mipmap-$d"
    Copy-Item $source "$dir/ic_launcher.png" -Force
    Copy-Item $source "$dir/ic_launcher_round.png" -Force
    
    if (Test-Path "$dir/ic_launcher.webp") {
        Remove-Item "$dir/ic_launcher.webp" -Force
    }
    if (Test-Path "$dir/ic_launcher_round.webp") {
        Remove-Item "$dir/ic_launcher_round.webp" -Force
    }
    
    Write-Host "Updated mipmap-$d" -ForegroundColor Green
}

Write-Host ""
Write-Host "App icon updated successfully!" -ForegroundColor Green
Write-Host "Rebuild and reinstall the app to see the new icon." -ForegroundColor Yellow
