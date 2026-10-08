plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    application
    alias(libs.plugins.spotless)
}

// Same Java as the app (JAVA_VERSION in build-logic/src/main/kotlin/AndroidConfig.kt).
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        // As in the app (build-logic, WarningsAsErrors.kt): CI passes -PwarningsAsErrors=true, then warnings fail
        // the build; locally they are only printed.
        allWarningsAsErrors.set(providers.gradleProperty("warningsAsErrors").map(String::toBoolean).orElse(false))
    }
}

application {
    mainClass.set("krio.systemdesign.shoppingapp.server.ApplicationKt")
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.logback.classic)

    testImplementation(kotlin("test"))
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.ktor.client.content.negotiation)
}

tasks.test {
    useJUnitPlatform()
    // Tests read data/: without this, an edited JSON file leaves the tests UP-TO-DATE and skipped.
    inputs.dir("data")
    // The message alone says what is wrong in a JSON file; the stack trace only hides it.
    testLogging {
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStackTraces = false
    }
}

// Same code style as the app: rules in the repo root .editorconfig, rule switches as in its build.gradle.kts.
spotless {
    val disabledRules = mapOf("ktlint_standard_blank-line-between-when-conditions" to "disabled")
    kotlin {
        target("src/**/*.kt")
        ktlint(libs.versions.ktlint.get())
            .setEditorConfigPath(rootDir.resolve("../.editorconfig"))
            .editorConfigOverride(disabledRules)
    }
    kotlinGradle {
        target("*.kts")
        ktlint(libs.versions.ktlint.get())
            .setEditorConfigPath(rootDir.resolve("../.editorconfig"))
            .editorConfigOverride(disabledRules)
    }
}
