# Run the gradlew command
.\gradlew runOnWindows

# Optional: Add error handling
if ($LASTEXITCODE -ne 0) {
    Write-Error "The '.\gradlew runOnWindows' command failed with exit code: $LASTEXITCODE"
}