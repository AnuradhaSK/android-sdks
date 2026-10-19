pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.android.application") version "8.2.2"
        id("org.jetbrains.kotlin.android") version "1.9.22"
        id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "AndroidPlayground"

// Build the SDK from this checkout: `dev.thunderid:android` resolves to the parent build.
includeBuild("..") {
    name = "android"
    dependencySubstitution {
        substitute(module("dev.thunderid:android")).using(project(":"))
    }
}
