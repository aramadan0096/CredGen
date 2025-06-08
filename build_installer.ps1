# Check if the script is running as administrator
if (-NOT ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)) {
    Write-Host "Restarting script with administrator privileges..."
    Start-Process powershell.exe -Verb RunAs -ArgumentList "-NoProfile", "-ExecutionPolicy Bypass", "-File", "`"$PSCommandPath`""
    exit # Exit the current non-elevated session
}

# --- Your script logic starts here (it will only run if elevated) ---

# Navigate to the directory where your gradlew script is located (if it's not in the current directory)
# Replace 'C:\path\to\your\project' with the actual path to your Gradle project
# Set-Location -Path "C:\path\to\your\project"

Write-Host "Running gradlew command..."
.\build\packaging\windows-x86_64\package.bat

# Optional: Add error handling
if ($LASTEXITCODE -ne 0) {
    Write-Error "The '.\build\packaging\windows-x86_64\package.bat' command failed with exit code: $LASTEXITCODE"
}