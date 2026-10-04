pluginManagement {
    // Convention plugins (shoppingapp.android.*) that set up the modules.
    includeBuild("build-logic")

    repositories {
        // Only Android and Google artifacts come from Google Maven, the rest from Maven Central.
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    // Downloads the JDK for the Gradle daemon (gradle/gradle-daemon-jvm.properties) if it isn't installed.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    // Repositories are declared only here: a module that declares its own fails the build.
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ShoppingApp"

include(":app")
include(":app-uikit")

include(":domain")
include(":data")

include(":core:ui")
include(":core:network")
include(":core:config")

include(":feature:catalog")
include(":feature:cart")
include(":feature:promo")
include(":feature:checkout")
include(":feature:settings")
