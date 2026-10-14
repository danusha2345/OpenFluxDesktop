rootProject.name = "openfluxdesktop"

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        // JOGL for JCEF (KCEF, the built-in browser).
        maven("https://jogamp.org/deployment/maven") {
            mavenContent { includeGroupAndSubgroups("org.jogamp") }
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// shared/ is the OpenFluxClientShared git submodule (init with
// `git submodule update --init --recursive`). It still targets Android too,
// so building here needs an Android SDK even though this repo only packages
// the desktop app.
include(":shared")
include(":desktopApp")

// Local, versioned fixes to the pinned upstream shared module.
val sharedSync = ProcessBuilder("bash", File(settingsDir, "scripts/sync-shared.sh").absolutePath)
    .directory(settingsDir).inheritIO().start()
check(sharedSync.waitFor() == 0) { "Could not apply the verified shared module patch" }
