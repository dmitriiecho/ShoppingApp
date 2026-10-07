plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    application
    alias(libs.plugins.spotless)
}

// Та же версия Java, что у приложения (JAVA_VERSION в build-logic/src/main/kotlin/AndroidConfig.kt).
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
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
    // Тесты читают файлы из data/. Без этой строки Gradle не заметит правку JSON и пропустит тесты как UP-TO-DATE.
    inputs.dir("data")
    // Печатать текст ошибки без стектрейса: по нему видно, что не так в JSON-файлах.
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
