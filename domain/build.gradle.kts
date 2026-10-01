plugins {
    alias(libs.plugins.kotlin.jvm)
}

// Та же версия Java, что у Android-модулей (JAVA_VERSION в build-logic/src/main/kotlin/AndroidConfig.kt).
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

dependencies {
    api(libs.kotlinx.collections.immutable)
    api(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
}
