import com.android.build.api.dsl.CommonExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the

// Shared by every Android module of the project.
internal object AndroidConfig {
    const val COMPILE_SDK = 37
    const val MIN_SDK = 26
    const val TARGET_SDK = 36

    // Also set in shared/domain/build.gradle.kts: a pure Kotlin module that build-logic doesn't configure.
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

internal val Project.libs: LibrariesForLibs
    get() = the<LibrariesForLibs>()
