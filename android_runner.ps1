# --- Script Configuration ---
$ProjectDir = Get-Location
$ApkPath = Join-Path $ProjectDir.Path "composeApp\build\outputs\apk\debug\composeApp-debug.apk"
$GradleFile = Join-Path $ProjectDir.Path "composeApp\build.gradle.kts"
$ManifestFile = Join-Path $ProjectDir.Path "composeApp\src\androidMain\AndroidManifest.xml"
if ($env:ANDROID_HOME) {
    $AdbPath = Join-Path $env:ANDROID_HOME "platform-tools\adb.exe"
    $EmulatorPath = Join-Path $env:ANDROID_HOME "emulator\emulator.exe"
} else {
    Write-Host "Error: ANDROID_HOME environment variable is not set."
    exit 1
}

# --- Functions ---
function Get-PackageName {
    param($File)
    $match = Select-String -Path $File -Pattern 'namespace\s*=\s*"([^"]+)"'
    if ($match) {
        return $match.Matches[0].Groups[1].Value
    }
    return $null
}

function Get-LauncherActivity {
    param($File)
    try {
        [xml]$manifest = Get-Content -Path $File -Raw
        $activityNode = $manifest.manifest.application.activity | ForEach-Object {
            $activity = $_
            $isLauncher = $_.'intent-filter'.category | Where-Object { $_.'android:name' -eq 'android.intent.category.LAUNCHER' }
            if ($isLauncher) {
                return $activity
            }
        } | Select-Object -First 1

        if ($activityNode) {
            return $activityNode.'android:name'
        }
    } catch {
        Write-Host "Warning: Could not parse AndroidManifest.xml. $_"
    }
    return "MainActivity" # Fallback
}

# --- Main Execution ---

# 1. Extract Package and Activity
Write-Host "Extracting package name from $GradleFile..."
$PackageName = Get-PackageName -File $GradleFile
if (-not $PackageName) {
    Write-Host "Error: Could not extract package name."
    exit 1
}
Write-Host "Using package: $PackageName"

Write-Host "Extracting launcher activity from $ManifestFile..."
$ActivityName = Get-LauncherActivity -File $ManifestFile
$MainActivity = if ($ActivityName.StartsWith(".")) {
    "$PackageName$ActivityName"
} elseif ($ActivityName.Contains(".")) {
    $ActivityName
} else {
    "$PackageName.$ActivityName"
}
Write-Host "Using activity: $MainActivity"

# 2. Build the APK
Write-Host "Building APK..."
& ./gradlew.bat composeApp:assembleDebug
if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed!"
    exit 1
}
if (-not (Test-Path $ApkPath)) {
    Write-Host "Build failed or APK not found at $ApkPath"
    exit 1
}
Write-Host "APK built successfully."

# 3. Find Device or Start Emulator
Write-Host "Checking for connected devices..."
$Device = (& $AdbPath devices) | Select-String -Pattern '\bdevice$' | Select-Object -First 1 | ForEach-Object { $_.Line.Split([char]0x9)[0] }

if (-not $Device) {
    Write-Host "No active devices found. Checking for available emulators..."
    $AvdList = & $EmulatorPath -list-avds
    if (-not $AvdList) {
        Write-Host "No available emulators found. Please create an AVD in Android Studio."
        exit 1
    }
    $AvdName = $AvdList | Select-Object -First 1
    Write-Host "Starting emulator: $AvdName..."
    Start-Process $EmulatorPath -ArgumentList "-avd $AvdName -no-snapshot-load"

    Write-Host "Waiting for emulator to boot..."
    for ($i = 0; $i -lt 30; $i++) {
        $bootComplete = & $AdbPath shell getprop sys.boot_completed 2>$null
        if ($bootComplete -and $bootComplete.Trim() -eq "1") {
            Write-Host "Emulator boot completed!"
            break
        }
        Start-Sleep -Seconds 5
    }
    Start-Sleep -Seconds 5 # Give it a moment to settle
    $Device = (& $AdbPath devices) | Select-String -Pattern '\bdevice$' | Select-Object -First 1 | ForEach-Object { $_.Line.Split([char]0x9)[0] }
}

if (-not $Device) {
    Write-Host "No device detected after trying to start an emulator. Exiting."
    exit 1
}
Write-Host "Device detected: $Device"

# 4. Install the APK
Write-Host "Installing APK on device..."
& $AdbPath -s $Device install -r $ApkPath

# 5. Launch the App
Write-Host "Launching the app..."
& $AdbPath -s $Device shell am start -n "$PackageName/$MainActivity"

Write-Host "Done!" 