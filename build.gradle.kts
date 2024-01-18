buildscript {
    dependencies {
        classpath("com.google.gms.google-services:com.google.gms.google-services.gradle.plugin:4.4.0")
    }
}

plugins {
    id("com.android.application") version "8.0.0" apply false
    id("com.android.library") version "8.0.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
}

tasks {
    register("clean", Delete::class) {
        delete(rootProject.buildDir)
    }
}