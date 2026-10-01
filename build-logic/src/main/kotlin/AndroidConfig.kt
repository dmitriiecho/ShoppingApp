import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

// Числа, общие для всех Android-модулей проекта: изменение здесь доходит до каждого модуля.
internal object AndroidConfig {
    const val COMPILE_SDK = 37
    const val MIN_SDK = 26
    const val TARGET_SDK = 36
    // Та же версия задана в domain/build.gradle.kts: это модуль на чистом Kotlin, build-logic его не настраивает.
    val JAVA_VERSION = JavaVersion.VERSION_21
}

internal fun configureAndroid(android: CommonExtension) {
    android.compileSdk {
        version = release(AndroidConfig.COMPILE_SDK)
    }
    android.defaultConfig.minSdk = AndroidConfig.MIN_SDK
    android.compileOptions.sourceCompatibility = AndroidConfig.JAVA_VERSION
    android.compileOptions.targetCompatibility = AndroidConfig.JAVA_VERSION
}

internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")
