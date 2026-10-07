// Plugins are declared here once with their versions; modules apply them without versions.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.spotless)
}

// Code style for the whole build (rules in .editorconfig): spotlessCheck checks, spotlessApply fixes.
// server/ is a separate build and has its own setup.
spotless {
    // Spotless ignores rule switches in .editorconfig, so they go here.
    // Blank lines between all branches of a when once one branch is multiline: noise in short whens.
    val disabledRules = mapOf("ktlint_standard_blank-line-between-when-conditions" to "disabled")
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**", "server/**")
        ktlint(libs.versions.ktlint.get())
            .setEditorConfigPath(rootDir.resolve(".editorconfig"))
            .editorConfigOverride(disabledRules)
    }
    kotlinGradle {
        target("**/*.kts")
        targetExclude("**/build/**", "server/**")
        ktlint(libs.versions.ktlint.get())
            .setEditorConfigPath(rootDir.resolve(".editorconfig"))
            .editorConfigOverride(disabledRules)
    }
}
