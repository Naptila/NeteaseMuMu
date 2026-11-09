pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        if (providers.environmentVariable("GITHUB_ACTIONS").orNull == "true") {
            maven(url = "https://maven-central.storage-download.googleapis.com/maven2") {
                name = "KotlinCentralMirror"
                content { includeGroup("org.jetbrains.kotlin") }
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        if (providers.environmentVariable("GITHUB_ACTIONS").orNull == "true") {
            maven(url = "https://maven-central.storage-download.googleapis.com/maven2") {
                name = "KotlinCentralMirror"
                content { includeGroup("org.jetbrains.kotlin") }
            }
        }
        mavenCentral()
        maven(url = "https://api.xposed.info")
    }
}

rootProject.name = "NeteaseMuMu"
include(":app")
