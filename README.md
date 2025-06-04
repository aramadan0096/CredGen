CredGen
========

Create beautiful film credits without the pain.

Visit https://cinecred.com for further information about the nature of this
software, as well as manifold download options.

CredGen is licensed under the GNU General Public License Version 3, or any
later version. For details, refer to the [license](LICENSE) file.

For information on how to compile, run, and maintain this software, refer to the
[maintenance](MAINTENANCE.md) file.

You are welcome to
[contribute translations](https://hosted.weblate.org/engage/cinecred/) on
Weblate! Here's how things currently stand:

![Translation status](https://hosted.weblate.org/widgets/cinecred/-/multi-auto.svg)


## How to Build CredGen
### Prerequisites
- JDK 21 : Required for building and running CredGen. Gradle's toolchain enforces this, but you must install JDK 21 manually (automatic download is disabled).
- Gradle : Use the included gradlew / gradlew.bat scripts for consistent builds.
- Platform-specific tools for native libraries:
  - Windows : Visual Studio Build Tools (MSVC and Clang, both selected in the installer)
  - macOS : Xcode command line tools
  - Linux : GCC and Clang
  - Skia : Python and Git (all platforms)
  - Linux packaging : dpkg-deb , rpmbuild , rpmsign (for DEB/RPM)

### Building and Running
.\gradlew runOnWindows
.\gradlew runOnMacX86
.\gradlew runOnMacARM
.\gradlew runOnLinux

### Packaging for Release
1. Prepare Packaging
   - Run: gradlew clean preparePackaging
   - This creates three folders in build/packaging/ , one for each OS.
2. Platform-specific Packaging
   - Windows : Copy the Windows folder to a Windows machine and run package.bat to build the installer.
   - macOS/Linux : Use package.sh for macOS x86, macOS ARM, and Linux.
   - Linux : For DEB/RPM, ensure you have dpkg-deb , rpmbuild , and rpmsign . To sign RPMs, configure your ~/.rpmmacros with the appropriate PGP key.
3. Upload Artifacts
   - Upload the resulting files from the out/ folders to your website or distribution channels.
   - The Linux script also prepares AUR and Flathub repos, but you must push manually.
### Additional Notes
- The build process is automated via Gradle tasks and scripts.
- For native library builds, follow the platform-specific requirements in the MAINTENANCE.md file.
- The Gradle build script ( build.gradle.kts ) manages dependencies, source sets, and packaging logic.
For more details, refer to the MAINTENANCE.md file and the comments in build.gradle.kts .