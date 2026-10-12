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

// The API formats shared with the server (contract/), a separate build so the server can include it too.
includeBuild("contract")

include(":apps:shop:android")
include(":apps:shop:app")
include(":apps:uikit:android")
include(":apps:uikit:app")

include(":core:designsystem")
include(":core:compose-utils")
include(":core:compose-utils:testing")
include(":core:network")
include(":core:network:testing")
include(":core:config")

include(":crossfeature:domain")
include(":crossfeature:domain:testing")
include(":crossfeature:data")
include(":crossfeature:ui")
include(":crossfeature:analytics")
include(":crossfeature:analytics:testing")

include(":feature:catalog:impl")
include(":feature:catalog:ui")
include(":feature:cart:impl")
include(":feature:cart:ui")
include(":feature:promo:impl")
include(":feature:promo:ui")
include(":feature:checkout:impl")
include(":feature:checkout:ui")
include(":feature:settings:impl")
