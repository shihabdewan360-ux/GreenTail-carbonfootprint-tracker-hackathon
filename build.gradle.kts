// build.gradle.kts (Project-level)
plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.4.0" apply false

    // ADD THIS LINE:
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}