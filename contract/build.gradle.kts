import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

// The coordinates the app and the server depend on: krio.systemdesign.shoppingapp:contract.
group = "krio.systemdesign.shoppingapp"

kotlin {
    // jvm: the server and the app's Android modules (they accept a JVM library); iOS: the app's shared code.
    jvm {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
    }
    iosArm64()
    iosSimulatorArm64()

    // As in the app and the server: CI passes -PwarningsAsErrors=true, then warnings fail the build.
    compilerOptions {
        allWarningsAsErrors.set(providers.gradleProperty("warningsAsErrors").map(String::toBoolean).orElse(false))
    }

    sourceSets {
        commonMain.dependencies {
            // api: the DTOs are @Serializable, and both sides read and write them with their own Json.
            api(libs.kotlinx.serialization.core)
        }
    }
}
