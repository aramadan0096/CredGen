package com.loadingbyte.credgen


enum class Platform(
    val label: String,
    val os: OS,
    val arch: Arch,
) {

    WINDOWS("windows", OS.WINDOWS, Arch.X86_64),
    MAC_X86("macX86", OS.MAC, Arch.X86_64),
    MAC_ARM("macARM", OS.MAC, Arch.ARM64),
    LINUX("linux", OS.LINUX, Arch.X86_64);

    val slug: String = os.slug + "-" + arch.slug
    val slugFlatLaf: String = os.slugFlatLaf + "-" + arch.slug
    val slugJavacpp: String = os.slugJavacpp + "-" + arch.slug


    enum class OS(
        val slug: String, val slugFlatLaf: String, val slugJavacpp: String,
        val libPrefix: String, val codeLibExt: String, val importLibExt: String
    ) {
        WINDOWS("windows", "windows", "windows", "", "dll", "lib"),
        MAC("mac", "macos", "macosx", "lib", "dylib", "dylib"),
        LINUX("linux", "linux", "linux", "lib", "so", "so");

        fun codeLib(name: String) = "$libPrefix$name.$codeLibExt"
        fun importLib(name: String) = "$libPrefix$name.$importLibExt"
    }


    enum class Arch(val slug: String, val slugTemurin: String, val slugWix: String) {
        X86_64("x86_64", "x64", "x64"),
        ARM64("arm64", "aarch64", "arm64")
    }

}
