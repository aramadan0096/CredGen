# Run the gradlew command
.\gradlew clean preparePackaging

# Optional: Add error handling
if ($LASTEXITCODE -ne 0) {
    Write-Error "The '.\gradlew clean preparePackaging' command failed with exit code: $LASTEXITCODE"
}