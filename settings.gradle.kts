pluginManagement {
    // Общие настройки модулей (convention plugins): compileSdk, minSdk, Compose, Hilt, набор зависимостей фичи.
    includeBuild("build-logic")
    repositories {
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
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ShoppingAppV2"
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
