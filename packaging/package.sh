#!/bin/bash

source settings/general

jdk_dir="work/jdk-$JDK_MAJOR$JDK_MINOR+$JDK_PATCH"

if [[ "@OS@" == mac ]]; then
  jdk_bin="$jdk_dir/Contents/Home/bin"
  jpackage_args="--name credgen --icon images/icon.icns --resource-dir resources/mac @settings/jpackage-mac"
elif [[ "@OS@" == linux ]]; then
  jdk_bin="$jdk_dir/bin"
  jpackage_args="--name credgen"
  for cmd in tar git fakeroot dpkg-deb rpmbuild rpmsign makepkg; do
    if ! command -v $cmd > /dev/null; then
      missing_cmds+=($cmd)
    fi
  done
  if [[ ! -z "$missing_cmds" ]]; then
    echo "The following required programs are not installed: ${missing_cmds[@]}"
    exit 1
  fi
else
  exit 1
fi

mkdir work/ out/

echo "Downloading and extracting Temurin..."
curl -L "https://github.com/adoptium/temurin$JDK_MAJOR-binaries/releases/download/jdk-$JDK_MAJOR$JDK_MINOR+$JDK_PATCH/OpenJDK${JDK_MAJOR}U-jdk_@ARCH_TEMURIN@_@OS@_hotspot_$JDK_MAJOR${JDK_MINOR}_$JDK_PATCH.tar.gz" | tar -xzf - -C work/

if [[ "@OS@" == linux ]]; then
  echo "Downloading appimagetool..."
  curl -LO --output-dir work/ "https://github.com/AppImage/appimagetool/releases/download/continuous/appimagetool-x86_64.AppImage"
  chmod +x work/appimagetool-x86_64.AppImage
fi

echo "Collecting minimized JRE..."
"$jdk_bin/jlink" @settings/jlink $jlink_args --output work/runtime/

echo "Collecting installation image..."
"$jdk_bin/jpackage" @settings/jpackage $jpackage_args --input app/ --runtime-image work/runtime/ --dest work/image/

if [[ "@OS@" == mac ]]; then
  cp resources/universal/LEGAL work/image/credgen.app/Contents/

  echo "Assembling TAR.GZ archive..."
  tar -czf out/credgen-@VERSION@-@OS@-@ARCH@.tar.gz -C work/image/ credgen.app/

  echo "Assembling PKG package..."
  pkgbuild --root work/image/ --component-plist resources/pkg/component.plist --identifier credgen --install-location /Applications work/credgen.pkg
  productbuild --distribution resources/pkg/distribution.dist --package-path work/ --resources images/ out/credgen-@VERSION@-@ARCH@.pkg
elif [[ "@OS@" == linux ]]; then
  cp resources/universal/LEGAL work/image/credgen/
  rm work/image/credgen/lib/credgen.png

  echo "Assembling TAR.GZ archive..."
  mkdir -p work/targz/credgen/
  cp -r work/image/credgen/* resources/linux/* images/* "$_"
  tar -czf out/credgen-@VERSION@-@OS@-@ARCH@.tar.gz -C work/targz/ credgen/

  echo "Assembling AppImage package..."
  mkdir -p work/AppDir/usr/
  cp -r work/image/credgen/* "$_"
  mkdir -p work/AppDir/usr/share/applications/
  cp resources/linux/credgen.desktop "$_"
  mkdir -p work/AppDir/usr/share/metainfo/
  cp resources/linux/credgen.metainfo.xml "$_/credgen.appdata.xml"
  mkdir -p work/AppDir/usr/share/icons/hicolor/scalable/apps/
  cp images/credgen.svg "$_"
  mkdir -p work/AppDir/usr/share/icons/hicolor/256x256/apps/
  cp images/credgen.png "$_"
  ln -s usr/bin/credgen work/AppDir/AppRun
  ln -s usr/share/applications/credgen.desktop work/AppDir/
  ln -s usr/share/icons/hicolor/scalable/apps/credgen.svg work/AppDir/
  ln -s usr/share/icons/hicolor/256x256/apps/credgen.png work/AppDir/
  ln -s credgen.svg work/AppDir/.DirIcon
  work/appimagetool-x86_64.AppImage --no-appstream work/AppDir out/credgen-@VERSION@-@ARCH@.appimage

  echo "Collecting DEB/RPM package tree..."
  mkdir -p work/tree/opt/credgen/
  cp -r work/image/credgen/* "$_"
  mkdir -p work/tree/usr/share/applications/
  cp resources/linux/credgen.desktop "$_"
  mkdir -p work/tree/usr/share/metainfo
  cp resources/linux/credgen.metainfo.xml "$_"
  mkdir -p work/tree/usr/share/icons/hicolor/scalable/apps/
  cp images/credgen.svg "$_"
  mkdir -p work/tree/usr/share/icons/hicolor/256x256/apps/
  cp images/credgen.png "$_"
  mkdir -p work/tree/usr/bin/
  ln -s /opt/credgen/bin/credgen "$_"

  echo "Assembling DEB package..."
  cp -r work/tree/ work/deb/
  mkdir -p work/deb/DEBIAN/
  cp resources/deb/control "$_"
  echo "Installed-Size: $(du -s work/deb/opt/ | cut -f1)" >> work/deb/DEBIAN/control
  fakeroot dpkg-deb --build work/deb out/credgen-@VERSION@-@ARCH@.deb

  echo "Assembling and signing RPM package..."
  rpmbuild --quiet -bb resources/rpm/credgen.spec --define "%_sourcedir $(pwd)/work/tree" --define "%_topdir $(pwd)/work/rpm" --define "%_rpmdir $(pwd)/out" --define "%_rpmfilename credgen-@VERSION@-@ARCH@.rpm"
  rpmsign --addsign out/*.rpm

  echo "Assembling AUR commit..."
  mkdir -p out/aur/
  git -C out/aur/ clone ssh://aur@aur.archlinux.org/credgen.git
  sed "s/{{SHA_256_HASH}}/$(sha256sum out/*.tar.gz | cut -d " " -f 1)/g" resources/aur/PKGBUILD > out/aur/credgen/PKGBUILD
  makepkg -D out/aur/credgen/ --printsrcinfo > out/aur/credgen/.SRCINFO
  git -C out/aur/credgen/ add -A
  git -C out/aur/credgen/ commit -m "Publish credgen @VERSION@"

  echo "Assembling Flathub commit..."
  mkdir -p out/flathub/
  git -C out/flathub/ clone git@github.com:flathub/com.credgen.credgen.git
  sed "s/{{SHA_256_HASH}}/$(sha256sum out/*.tar.gz | cut -d " " -f 1)/g" resources/flathub/com.credgen.credgen.yml > out/flathub/com.credgen.credgen/com.credgen.credgen.yml
  git -C out/flathub/com.credgen.credgen/ add -A
  git -C out/flathub/com.credgen.credgen/ commit -m "Publish credgen @VERSION@"
fi

echo "Cleaning up..."
rm -rf work/
